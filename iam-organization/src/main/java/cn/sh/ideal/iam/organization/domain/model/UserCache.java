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
import java.util.concurrent.TimeUnit;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Component
public class UserCache {
    private static final Cache<Long, UserCacheWrapper> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();
    private final IamI18nReader i18nReader;
    private final UserRepository userRepository;
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
        User user = get(userId);
        if (user == null) {
            log.info("获取用户缓存信息失败, 用户不存在: {}", userId);
            throw new ResourceNotFoundException(i18nReader.getMessage("user.not_found"));
        }
        return user;
    }

    @Nullable
    public User get(long userId) {
        UserCacheWrapper wrapper = getCacheWrapper(userId);
        Long userChangeTime = userChangeCache.getIfPresent(userId);
        if (userChangeTime != null && userChangeTime > wrapper.cachedTime) {
            CACHE.invalidate(userId);
            wrapper = getCacheWrapper(userId);
        }
        return wrapper.user();
    }

    public void invalidate(long userId) {
        userChangeCache.put(userId, System.currentTimeMillis());
    }

    @Nonnull
    private UserCacheWrapper getCacheWrapper(long userId) {
        return CACHE.get(userId, k -> {
            long currentTimeMillis = System.currentTimeMillis();
            User user = userRepository.findById(userId).orElse(null);
            return new UserCacheWrapper(user, currentTimeMillis);
        });
    }

    private record UserCacheWrapper(@Nullable User user, long cachedTime) {

    }
}
