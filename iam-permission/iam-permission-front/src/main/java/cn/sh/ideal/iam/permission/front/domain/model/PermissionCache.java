package cn.sh.ideal.iam.permission.front.domain.model;

import cn.idealio.framework.cache.Cache;
import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.concurrent.Asyncs;
import cn.idealio.framework.lang.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 宋志宗 on 2024/5/17
 */
@Slf4j
@Component
public class PermissionCache implements InitializingBean, ApplicationRunner, PermissionRepositoryListener {
    private static final String PERMISSION_CHANGED_TIME_KEY = "iam:permission:change:time";
    private final Lock refreshLock = new ReentrantLock();
    private final AtomicLong lastRefreshTime = new AtomicLong(0);
    private final PermissionRepository permissionRepository;
    private final Cache<String, Long> permissionChangedTimeCache;

    private volatile Map<Long, Permission> permissionMap = new HashMap<>();
    private volatile Map<Long, List<Permission>> itemPermissionsMap = new HashMap<>();

    public PermissionCache(@Nonnull CacheFactory cacheFactory,
                           @Nonnull PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        this.permissionChangedTimeCache = cacheFactory.
                <String, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(Duration.ofHours(1))
                .build("");
    }

    @Nullable
    public Permission findById(long permissionId) {
        return permissionMap.get(permissionId);
    }

    @Nonnull
    public List<Permission> findAllById(@Nonnull Set<Long> permissionIds) {
        ArrayList<Permission> permissions = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            Permission permission = permissionMap.get(permissionId);
            if (permission != null) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    @Nonnull
    public List<Permission> findAllByItemIdIn(@Nonnull Set<Long> itemIds) {
        List<Permission> permissions = new ArrayList<>();
        for (Long itemId : itemIds) {
            List<Permission> itemPermissions = itemPermissionsMap.get(itemId);
            if (Lists.isEmpty(itemPermissions)) {
                continue;
            }
            permissions.addAll(itemPermissions);
        }
        return permissions;
    }

    @Nonnull
    public List<Permission> findAllByItemId(long itemId) {
        return itemPermissionsMap.getOrDefault(itemId, new ArrayList<>());
    }

    private void refresh() {
        boolean tryLock = refreshLock.tryLock();
        if (!tryLock) {
            log.warn("刷新权限缓存出现锁冲突, 等待下一轮执行");
            return;
        }
        long nanoTime = System.nanoTime();
        try {
            lastRefreshTime.set(System.currentTimeMillis());
            List<Permission> permissions = permissionRepository.findAll();
            if (permissions.isEmpty()) {
                return;
            }
            Map<Long, Permission> permissionMap = new HashMap<>();
            Map<Long, List<Permission>> itemPermissionsMap = new HashMap<>();
            for (Permission permission : permissions) {
                boolean available = permission.available();
                if (!available) {
                    continue;
                }
                Long permissionId = permission.getId();
                permissionMap.put(permissionId, permission);
                long itemId = permission.getItemId();
                itemPermissionsMap.computeIfAbsent(itemId, k -> new ArrayList<>()).add(permission);
            }
            this.permissionMap = permissionMap;
            this.itemPermissionsMap = itemPermissionsMap;
            if (log.isInfoEnabled()) {
                long millis = (System.nanoTime() - nanoTime) / 1000_000;
                log.info("刷新权限缓存完成, 共计: {} 条权限, 耗时 {}ms", permissions.size(), millis);
            }
        } finally {
            refreshLock.unlock();
        }
    }

    @Override
    public void afterPropertiesSet() {
        permissionRepository.addListener(this);
    }

    @Override
    public void run(ApplicationArguments args) {
        refresh();
        Duration duration = Duration.ofSeconds(5);
        Asyncs.scheduleAtFixedRate(duration, duration, () -> {
            try {
                Long permissionChangedTime = permissionChangedTimeCache.getIfPresent(PERMISSION_CHANGED_TIME_KEY);
                if (permissionChangedTime == null || permissionChangedTime < lastRefreshTime.get()) {
                    return;
                }
                log.info("发现权限变更, 开始刷新权限缓存...");
                refresh();
            } catch (Exception exception) {
                log.warn("刷新权限缓存出现异常: ", exception);
            }
        });
    }

    @Override
    public void onPermissionTableChanged() {
        permissionChangedTimeCache.put(PERMISSION_CHANGED_TIME_KEY, System.currentTimeMillis());
    }
}
