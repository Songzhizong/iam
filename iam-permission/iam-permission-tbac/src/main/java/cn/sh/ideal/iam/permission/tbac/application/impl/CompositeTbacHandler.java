package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.lang.Tuple;
import cn.sh.ideal.iam.permission.tbac.application.TbacHandler;
import cn.sh.ideal.iam.permission.tbac.configure.TbacProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/18
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class CompositeTbacHandler implements TbacHandler {
    private final TbacProperties properties;
    private final CachelessTbacHandler cachelessTbacHandler;
    private final CacheableTbacHandler cacheableTbacHandler;

    /**
     * 指定权限标识, 获取这个权限标识在各个容器节点上的权限配置信息
     *
     * @param userId    用户ID
     * @param authority 权限标识
     * @return [authority]有权限配置的containerId -> 是否分配 -> 是否继承
     */
    @Nonnull
    @Override
    public Map<Long, Tuple<Boolean, Boolean>>
    authorityContainerAssignInfo(long userId, @Nonnull String authority) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.authorityContainerAssignInfo(userId, authority);
        }
        return cachelessTbacHandler.authorityContainerAssignInfo(userId, authority);
    }

    @Nonnull
    @Override
    public Set<Long> visiblePermissionIds(long userId, long containerId) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.visiblePermissionIds(userId, containerId);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.visiblePermissionIds(userId, containerId);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[获取用户在指定安全容器上所有可见的权限ID]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public Set<Long> authorityContainerIds(long userId,
                                           @Nonnull String authority,
                                           @Nullable Long baseContainerId) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.authorityContainerIds(userId, authority, baseContainerId);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.authorityContainerIds(userId, authority, baseContainerId);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[获取用户拥有指定权限的容器id列表]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public Set<Long> containerPermissionIds(long userId, long containerId,
                                            @Nonnull Set<Long> permissionIds) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.containerPermissionIds(userId, containerId, permissionIds);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.containerPermissionIds(userId, containerId, permissionIds);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[过滤用户在指定安全容器上有权限的权限ID列表]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public Map<Long, Set<Long>> containerPermissionIds(long userId,
                                                       @Nonnull Set<Long> containerIds,
                                                       @Nonnull Set<Long> permissionIds) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.containerPermissionIds(userId, containerIds, permissionIds);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.containerPermissionIds(userId, containerIds, permissionIds);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[批量过滤用户在指定安全容器上有权限的权限ID列表]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean hasAuthority(long userId, long containerId, @Nonnull String authority) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.hasAuthority(userId, containerId, authority);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.hasAuthority(userId, containerId, authority);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[判断用户在指定安全容器上是否拥有指定权限]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean hasAnyAuthority(long userId, long containerId,
                                   @Nonnull Collection<String> authorities) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.hasAnyAuthority(userId, containerId, authorities);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.hasAnyAuthority(userId, containerId, authorities);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[判断用户在指定安全容器上是否拥有任一权限]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean hasAllAuthority(long userId, long containerId,
                                   @Nonnull Collection<String> authorities) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.hasAllAuthority(userId, containerId, authorities);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.hasAllAuthority(userId, containerId, authorities);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[判断用户在指定安全容器上是否拥有所有权限]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean hasApiPermission(long userId, long containerId,
                                    @Nonnull String method, @Nonnull String path) {
        if (properties.isEnableCache()) {
            return cacheableTbacHandler.hasApiPermission(userId, containerId, method, path);
        }
        long nanoTime = System.nanoTime();
        try {
            return cachelessTbacHandler.hasApiPermission(userId, containerId, method, path);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("非缓存执行[判断用户是否拥有API接口的访问权限]耗时 {}ms", millis);
            }
        }
    }
}
