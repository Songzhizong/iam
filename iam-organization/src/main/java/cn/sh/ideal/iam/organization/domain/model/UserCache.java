package cn.sh.ideal.iam.organization.domain.model;

import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户信息缓存
 * <p>
 * 使用{@link Caffeine}缓存用户信息,
 * 并使用分布式缓存记录用户信息变更时间,
 * 以便各个节点能够及时感知用户信息变更并更新缓存.
 * <p>
 * 为了避免每次访问都需要从远程缓存中获取用户信息变更时间,
 * 会设置一个缓存同步窗口时间,
 * 在窗口时间内不会从远程缓存中获取用户信息变更时间.
 *
 * @author 宋志宗 on 2024/5/30
 */
@Slf4j
@Component
public class UserCache {
    /** 用户信息缓存, 直接存储在内存中 */
    private static final Cache<Long, UserCacheWrapper> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();
    /** 缓存同步窗口时间, 用于控制缓存同步频率 */
    private static final long CACHE_SYNC_WINDOW_MILLIS = 2000;
    private final IamI18nReader i18nReader;
    private final UserRepository userRepository;
    /** 用户信息变更时间缓存, 使用分布式缓存, 方便在集群中共享信息 */
    private final cn.idealio.framework.cache.Cache<Long, Long> userChangeCache;

    public UserCache(@Nonnull IamI18nReader i18nReader,
                     @Nonnull CacheFactory cacheFactory,
                     @Nonnull UserRepository userRepository) {
        this.i18nReader = i18nReader;
        this.userRepository = userRepository;
        this.userChangeCache = cacheFactory.<Long, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(Duration.ofHours(2)).build("iam:user:change_time");
    }

    @Nonnull
    public User require(long userId) {
        return get(userId).orElseThrow(() -> {
            log.info("获取用户缓存信息失败, 用户不存在: {}", userId);
            String message = i18nReader.getMessage1("user.not_found", userId);
            return new ResourceNotFoundException(message);
        });
    }

    @Nonnull
    public Optional<User> get(long userId) {
        UserCacheWrapper wrapper = getCacheWrapper(userId);
        long currentTimeMillis = System.currentTimeMillis();
        // 并不是每次都需要从远程缓存中同步用户最近一次变更时间, 如果最近一次同步时间在窗口时间内, 则不需要同步
        if (currentTimeMillis - wrapper.latestSyncTime().get() < CACHE_SYNC_WINDOW_MILLIS) {
            return Optional.ofNullable(wrapper.user());
        }
        // 从远程缓存中获取用户信息最近一次变更时间, 如果变更时间大于缓存时间, 则需要重新加载用户信息
        Long userChangeTime = userChangeCache.getIfPresent(userId);
        if (userChangeTime != null && userChangeTime > wrapper.cachedTime) {
            CACHE.invalidate(userId);
            wrapper = getCacheWrapper(userId);
        } else {
            wrapper.latestSyncTime().set(currentTimeMillis);
        }
        return Optional.ofNullable(wrapper.user());
    }

    public void invalidate(long userId) {
        userChangeCache.put(userId, System.currentTimeMillis());
    }

    @Nonnull
    private UserCacheWrapper getCacheWrapper(long userId) {
        return CACHE.get(userId, k -> {
            long currentTimeMillis = System.currentTimeMillis();
            User user = userRepository.findById(userId).orElse(null);
            AtomicLong latestSyncTime = new AtomicLong(currentTimeMillis);
            return new UserCacheWrapper(currentTimeMillis, user, latestSyncTime);
        });
    }

    private record UserCacheWrapper(long cachedTime,
                                    @Nullable User user,
                                    @Nonnull AtomicLong latestSyncTime) {

    }
}
