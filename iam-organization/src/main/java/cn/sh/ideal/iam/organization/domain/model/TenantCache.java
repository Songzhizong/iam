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

/**
 * @author 宋志宗 on 2024/5/31
 */
@Slf4j
@Component
public class TenantCache {
    private static final Cache<Long, TenantCacheWrapper> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
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
        Long tenantChangeTime = tenantChangeCache.getIfPresent(tenantId);
        if (tenantChangeTime != null && tenantChangeTime > wrapper.cacheTime) {
            CACHE.invalidate(tenantId);
            wrapper = getCacheWrapper(tenantId);
        }
        return Optional.ofNullable(wrapper.tenant());
    }

    public void invalidate(long tenantId) {
        tenantChangeCache.put(tenantId, System.currentTimeMillis());
    }

    @Nonnull
    private TenantCacheWrapper getCacheWrapper(long tenantId) {
        return CACHE.get(tenantId, k -> {
            long currentTimeMillis = System.currentTimeMillis();
            Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
            return new TenantCacheWrapper(tenant, currentTimeMillis);
        });
    }


    record TenantCacheWrapper(@Nullable Tenant tenant, long cacheTime) {
    }
}
