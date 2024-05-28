package cn.sh.ideal.iam.organization.domain.model;

import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
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
public class PlatformCache {
    private static final Cache<String, PlatformCacheWrapper> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    private final OrganizationI18nReader i18nReader;
    private final PlatformRepository platformRepository;
    private final cn.idealio.framework.cache.Cache<String, Long> platformChangeCache;

    public PlatformCache(@Nonnull CacheFactory cacheFactory,
                         @Nonnull OrganizationI18nReader i18nReader,
                         @Nonnull PlatformRepository platformRepository) {
        this.i18nReader = i18nReader;
        this.platformRepository = platformRepository;
        this.platformChangeCache = cacheFactory.<String, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(Duration.ofHours(2)).build("iam:platform:change_time");
    }

    @Nonnull
    public Platform require(@Nonnull String code) {
        Platform platform = get(code);
        if (platform == null) {
            log.info("获取平台缓存信息失败, 平台不存在: {}", code);
            throw new ResourceNotFoundException(i18nReader.getMessage("platform.not.found"));
        }
        return platform;
    }


    @Nullable
    public Platform get(@Nonnull String code) {
        PlatformCacheWrapper wrapper = getCacheWrapper(code);
        Long platformChangeTime = platformChangeCache.getIfPresent(code);
        if (platformChangeTime != null && platformChangeTime > wrapper.cacheTime) {
            CACHE.invalidate(code);
            wrapper = getCacheWrapper(code);
        }
        return wrapper.platform();
    }

    public void invalidate(@Nonnull String code) {
        platformChangeCache.put(code, System.currentTimeMillis());
    }

    @Nonnull
    private PlatformCacheWrapper getCacheWrapper(@Nonnull String code) {
        return CACHE.get(code, k -> {
            long currentTimeMillis = System.currentTimeMillis();
            Platform platform = platformRepository.findByCode(code).orElse(null);
            return new PlatformCacheWrapper(platform, currentTimeMillis);
        });
    }

    record PlatformCacheWrapper(@Nullable Platform platform, long cacheTime) {
    }
}
