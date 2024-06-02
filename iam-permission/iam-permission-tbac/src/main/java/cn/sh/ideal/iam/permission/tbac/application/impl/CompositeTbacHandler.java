package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.lang.Tuple;
import cn.sh.ideal.iam.permission.tbac.application.TbacHandler;
import cn.sh.ideal.iam.permission.tbac.configure.TbacProperties;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignable;
import cn.sh.ideal.iam.security.api.AccessibleTenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.SequencedCollection;
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
    authorityContainerAssignInfo(@Nonnull Long userId, @Nonnull String authority) {
        if (properties.isCacheEnabled()) {
            return cacheableTbacHandler.authorityContainerAssignInfo(userId, authority);
        }
        return cachelessTbacHandler.authorityContainerAssignInfo(userId, authority);
    }

    @Nonnull
    @Override
    public Map<Long, Tuple<Boolean, Boolean>>
    permissionContainerAssignInfo(@Nonnull Long userId, @Nonnull Long permissionId) {
        if (properties.isCacheEnabled()) {
            return cacheableTbacHandler.permissionContainerAssignInfo(userId, permissionId);
        }
        return cachelessTbacHandler.permissionContainerAssignInfo(userId, permissionId);
    }

    @Nonnull
    @Override
    public Set<Long> visiblePermissionIds(@Nonnull Long userId,
                                          @Nonnull Long containerId,
                                          @Nonnull Long appId) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.visiblePermissionIds(userId, containerId, appId);
            }
            return cachelessTbacHandler.visiblePermissionIds(userId, containerId, appId);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[获取用户在指定安全容器上所有可见的权限ID]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public Set<Long> authorityContainerIds(@Nonnull Long userId,
                                           @Nonnull String authority,
                                           @Nullable Long baseContainerId) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.authorityContainerIds(userId, authority, baseContainerId);
            }
            return cachelessTbacHandler.authorityContainerIds(userId, authority, baseContainerId);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[获取用户拥有指定权限的容器id列表]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public Set<Long> containerPermissionIds(@Nonnull Long userId, @Nonnull Long containerId,
                                            @Nonnull Set<Long> permissionIds) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.containerPermissionIds(userId, containerId, permissionIds);
            }
            return cachelessTbacHandler.containerPermissionIds(userId, containerId, permissionIds);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[过滤用户在指定安全容器上有权限的权限ID列表]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public Map<Long, Set<Long>> containerPermissionIds(@Nonnull Long userId,
                                                       @Nonnull Set<Long> containerIds,
                                                       @Nonnull Set<Long> permissionIds) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.containerPermissionIds(userId, containerIds, permissionIds);
            }
            return cachelessTbacHandler.containerPermissionIds(userId, containerIds, permissionIds);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[批量过滤用户在指定安全容器上有权限的权限ID列表]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean hasAuthority(@Nonnull Long userId,
                                @Nonnull Long containerId,
                                @Nonnull String authority) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.hasAuthority(userId, containerId, authority);
            }
            return cachelessTbacHandler.hasAuthority(userId, containerId, authority);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[判断用户在指定安全容器上是否拥有指定权限]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean hasAnyAuthority(@Nonnull Long userId, @Nonnull Long containerId,
                                   @Nonnull Set<String> authorities) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.hasAnyAuthority(userId, containerId, authorities);
            }
            return cachelessTbacHandler.hasAnyAuthority(userId, containerId, authorities);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[判断用户在指定安全容器上是否拥有任一权限]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean hasApiPermission(@Nonnull Long userId, @Nonnull Long containerId,
                                    @Nonnull String method, @Nonnull String path) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.hasApiPermission(userId, containerId, method, path);
            }
            return cachelessTbacHandler.hasApiPermission(userId, containerId, method, path);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[判断用户是否拥有API接口的访问权限]耗时 {}ms", millis);
            }
        }
    }

    @Override
    public boolean needMfa(@Nonnull Long userId, @Nonnull Long containerId, @Nonnull Long permissionId) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.needMfa(userId, containerId, permissionId);
            }
            return cachelessTbacHandler.needMfa(userId, containerId, permissionId);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[判断用户是否需要双因素认证]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public PermissionAssignable assignable(@Nonnull Long userId,
                                           @Nonnull Long containerId,
                                           @Nonnull Long appId) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.assignable(userId, containerId, appId);
            }
            return cachelessTbacHandler.assignable(userId, containerId, appId);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[获取用户可分配信息]耗时 {}ms", millis);
            }
        }
    }

    @Nonnull
    @Override
    public SequencedCollection<AccessibleTenant> accessibleTenants(@Nonnull Long userId) {
        long nanoTime = System.nanoTime();
        try {
            if (properties.isCacheEnabled()) {
                return cacheableTbacHandler.accessibleTenants(userId);
            }
            return cachelessTbacHandler.accessibleTenants(userId);
        } finally {
            if (log.isDebugEnabled()) {
                long micros = (System.nanoTime() - nanoTime) / 1000;
                double millis = micros / 1000D;
                log.debug("执行[获取用户可访问的租户列表]耗时 {}ms", millis);
            }
        }
    }
}
