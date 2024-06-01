package cn.sh.ideal.iam.organization.domain.model;

import cn.idealio.framework.cache.Cache;
import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.concurrent.Asyncs;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 平台信息缓存
 *
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Component
public class PlatformCache implements InitializingBean, ApplicationRunner, PlatformRepositoryListener {
    private static final String PLATFORM_CHANGED_TIME_KEY = "iam:platform:change:time";
    private final Lock refreshLock = new ReentrantLock();
    private final AtomicLong lastRefreshTime = new AtomicLong(0);
    private final IamI18nReader i18nReader;
    private final PlatformRepository platformRepository;
    private final Cache<String, Long> platformChangedTimeCache;

    @Nonnull
    private volatile Map<String, Platform> platformMap = new HashMap<>();

    public PlatformCache(@Nonnull CacheFactory cacheFactory,
                         @Nonnull IamI18nReader i18nReader,
                         @Nonnull PlatformRepository platformRepository) {
        this.i18nReader = i18nReader;
        this.platformRepository = platformRepository;
        this.platformChangedTimeCache = cacheFactory.
                <String, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(Duration.ofHours(1))
                .build("");
    }

    public Platform require(@Nonnull String code) {
        Platform platform = findById(code).orElse(null);
        if (platform == null) {
            log.info("获取平台缓存信息失败, 平台不存在: {}", code);
            throw new ResourceNotFoundException(i18nReader.getMessage("platform.not.found"));
        }
        return platform;
    }

    @Nonnull
    public Optional<Platform> findById(String code) {
        return Optional.ofNullable(platformMap.get(code));
    }

    private void refresh() {
        if (!refreshLock.tryLock()) {
            return;
        }
        try {
            lastRefreshTime.set(System.currentTimeMillis());
            List<Platform> apps = platformRepository.findAll();
            this.platformMap = apps.stream().collect(Collectors.toMap(Platform::getCode, app -> app));
        } finally {
            refreshLock.unlock();
        }
    }


    @Override
    public void afterPropertiesSet() {
        platformRepository.addListener(this);
        refresh();
    }

    @Override
    public void run(ApplicationArguments args) {
        Duration duration = Duration.ofSeconds(5);
        Asyncs.scheduleAtFixedRate(duration, duration, () -> {
            try {
                Long permissionChangedTime = platformChangedTimeCache.getIfPresent(PLATFORM_CHANGED_TIME_KEY);
                if (permissionChangedTime == null || permissionChangedTime < lastRefreshTime.get()) {
                    return;
                }
                log.info("发现平台信息变更, 开始刷新缓存...");
                refresh();
            } catch (Exception exception) {
                log.warn("刷新平台信息缓存出现异常: ", exception);
            }
        });

    }

    @Override
    public void onPlatformTableChanged() {
        long value = System.currentTimeMillis() + 2000;
        platformChangedTimeCache.put(PLATFORM_CHANGED_TIME_KEY, value);

    }
}
