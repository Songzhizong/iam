package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.idealio.framework.concurrent.Asyncs;
import cn.idealio.framework.lang.TreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 宋志宗 on 2024/5/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityContainerCache implements InitializingBean {
    private final Lock refreshLock = new ReentrantLock();
    private final AtomicLong lastRefreshTime = new AtomicLong(0);
    private final SecurityContainerRepository securityContainerRepository;
    private final List<SecurityContainerCacheRefreshListener> refreshListeners;

    private volatile Map<Long, AnalyzedSecurityContainer> containerMap = Map.of();

    @Nullable
    public AnalyzedSecurityContainer findById(long id) {
        return containerMap.get(id);
    }

    @Nonnull
    public List<AnalyzedSecurityContainer> findAllById(@Nonnull Collection<Long> ids) {
        List<AnalyzedSecurityContainer> containers = new ArrayList<>();
        for (Long id : ids) {
            AnalyzedSecurityContainer analyzed = containerMap.get(id);
            if (analyzed == null) {
                continue;
            }
            containers.add(analyzed);
        }
        return containers;
    }

    @SuppressWarnings("DuplicatedCode")
    public void refresh() {
        if (!securityContainerRepository.existsByUpdatedTimeGte(lastRefreshTime.get())) {
            return;
        }
        log.info("发现安全容器更新, 开始刷新缓存...");
        boolean tryLock = refreshLock.tryLock();
        if (!tryLock) {
            log.warn("刷新安全容器缓存出现锁冲突, 等待下一轮执行");
            return;
        }
        long nanoTime = System.nanoTime();
        try {
            lastRefreshTime.set(System.currentTimeMillis());
            List<SecurityContainer> containers = securityContainerRepository.findAll();
            if (containers.isEmpty()) {
                return;
            }
            List<AnalyzedSecurityContainer> analyzedList = new ArrayList<>();
            Map<Long, AnalyzedSecurityContainer> analyzedMap = new HashMap<>();
            for (SecurityContainer container : containers) {
                Long containerId = container.getId();
                SequencedSet<Long> parentIds = container.parentIds();
                AnalyzedSecurityContainer analyzed = new AnalyzedSecurityContainer(container, parentIds);
                analyzedList.add(analyzed);
                analyzedMap.put(containerId, analyzed);
            }
            TreeNode.toTreeList(analyzedList);
            this.containerMap = analyzedMap;
            if (log.isInfoEnabled()) {
                long millis = (System.nanoTime() - nanoTime) / 1000_000;
                log.info("刷新安全容器缓存完成, 共计: {} 条数据, 耗时 {}ms", containers.size(), millis);
            }
        } finally {
            refreshLock.unlock();
        }
        for (SecurityContainerCacheRefreshListener refreshListener : refreshListeners) {
            Asyncs.exec(refreshListener::onSecurityContainerCacheRefreshed);
        }
    }

    @Override
    public void afterPropertiesSet() {
        refresh();
        Duration duration = Duration.ofSeconds(10);
        Asyncs.scheduleAtFixedRate(duration, duration, () -> {
            try {
                refresh();
            } catch (Exception exception) {
                log.warn("刷新安全容器缓存出现异常: ", exception);
            }
        });
    }
}
