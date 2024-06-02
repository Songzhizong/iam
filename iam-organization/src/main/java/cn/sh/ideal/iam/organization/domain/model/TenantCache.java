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
 * 租户信息缓存
 * <p>
 * 使用{@link Caffeine}缓存租户信息,
 * 并使用分布式缓存记录租户信息变更时间,
 * 以便各个节点能够及时感知租户信息变更并更新缓存.
 * <p>
 * 为了避免每次访问都需要从远程缓存中获取租户信息变更时间,
 * 会设置一个缓存同步窗口时间,
 * 在窗口时间内不会从远程缓存中获取租户信息变更时间.
 *
 * @author 宋志宗 on 2024/5/31
 */
@Slf4j
@Component
public class TenantCache {
    private static final Cache<Long, TenantCacheWrapper> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    /** 缓存同步窗口时间, 用于控制缓存同步频率 */
    private static final long CACHE_SYNC_WINDOW_MILLIS = 2000;
    private final IamI18nReader i18nReader;
    private final TenantRepository tenantRepository;
    private final cn.idealio.framework.cache.Cache<Long, Long> tenantChangeCache;

    public TenantCache(@Nonnull IamI18nReader i18nReader,
                       @Nonnull CacheFactory cacheFactory,
                       @Nonnull TenantRepository tenantRepository) {
        this.i18nReader = i18nReader;
        this.tenantRepository = tenantRepository;
        this.tenantChangeCache = cacheFactory.<Long, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(Duration.ofHours(2)).build("iam:tenant:change_time");
    }

    @Nonnull
    public Tenant require(@Nonnull Long tenantId) {
        return get(tenantId).orElseThrow(() -> {
            log.info("获取租户缓存信息失败, 租户不存在: {}", tenantId);
            String message = i18nReader.getMessage1("tenant.notfound", tenantId);
            return new ResourceNotFoundException(message);
        });
    }

    @Nonnull
    public Optional<Tenant> get(@Nonnull Long tenantId) {
        TenantCacheWrapper wrapper = getCacheWrapper(tenantId);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - wrapper.latestSyncTime().get() < CACHE_SYNC_WINDOW_MILLIS) {
            return Optional.ofNullable(wrapper.tenant());
        }
        Long tenantChangeTime = tenantChangeCache.getIfPresent(tenantId);
        if (tenantChangeTime != null && tenantChangeTime > wrapper.cacheTime) {
            CACHE.invalidate(tenantId);
            wrapper = getCacheWrapper(tenantId);
        } else {
            wrapper.latestSyncTime().set(currentTimeMillis);
        }
        return Optional.ofNullable(wrapper.tenant());
    }

    public void invalidate(@Nonnull Long tenantId) {
        tenantChangeCache.put(tenantId, System.currentTimeMillis());
    }

    @Nonnull
    private TenantCacheWrapper getCacheWrapper(@Nonnull Long tenantId) {
        return CACHE.get(tenantId, k -> {
            long currentTimeMillis = System.currentTimeMillis();
            Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
            AtomicLong latestSyncTime = new AtomicLong(currentTimeMillis);
            return new TenantCacheWrapper(currentTimeMillis, tenant, latestSyncTime);
        });
    }


    record TenantCacheWrapper(long cacheTime,
                              @Nullable Tenant tenant,
                              @Nonnull AtomicLong latestSyncTime) {
    }
}
