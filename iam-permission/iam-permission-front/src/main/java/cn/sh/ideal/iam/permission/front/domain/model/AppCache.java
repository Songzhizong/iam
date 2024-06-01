package cn.sh.ideal.iam.permission.front.domain.model;

import cn.idealio.framework.cache.Cache;
import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.concurrent.Asyncs;
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
 * @author 宋志宗 on 2024/5/31
 */
@Slf4j
@Component
public class AppCache implements InitializingBean, ApplicationRunner, AppRepositoryListener {
    private static final String APP_CHANGED_TIME_KEY = "iam:app:change:time";
    private final Lock refreshLock = new ReentrantLock();
    private final AtomicLong lastRefreshTime = new AtomicLong(0);
    private final AppRepository appRepository;
    private final Cache<String, Long> appChangedTimeCache;

    private volatile Map<Long, App> appMap = new HashMap<>();

    public AppCache(@Nonnull CacheFactory cacheFactory,
                    @Nonnull AppRepository appRepository) {
        this.appRepository = appRepository;
        this.appChangedTimeCache = cacheFactory.
                <String, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(Duration.ofHours(1))
                .build("");
    }

    @Nonnull
    public Optional<App> findById(long appId) {
        return Optional.ofNullable(appMap.get(appId));
    }

    private void refresh() {
        if (!refreshLock.tryLock()) {
            return;
        }
        try {
            lastRefreshTime.set(System.currentTimeMillis());
            List<App> apps = appRepository.findAll();
            this.appMap = apps.stream().collect(Collectors.toMap(App::getId, app -> app));
        } finally {
            refreshLock.unlock();
        }
    }

    @Override
    public void afterPropertiesSet() {
        appRepository.addListener(this);
        refresh();
    }

    @Override
    public void run(ApplicationArguments args) {
        Duration duration = Duration.ofSeconds(5);
        Asyncs.scheduleAtFixedRate(duration, duration, () -> {
            try {
                Long permissionChangedTime = appChangedTimeCache.getIfPresent(APP_CHANGED_TIME_KEY);
                if (permissionChangedTime == null || permissionChangedTime < lastRefreshTime.get()) {
                    return;
                }
                log.info("发现应用信息变更, 开始刷新缓存...");
                refresh();
            } catch (Exception exception) {
                log.warn("刷新应用缓存出现异常: ", exception);
            }
        });
    }

    @Override
    public void onAppTableChanged() {
        long value = System.currentTimeMillis() + 2000;
        appChangedTimeCache.put(APP_CHANGED_TIME_KEY, value);
    }
}
