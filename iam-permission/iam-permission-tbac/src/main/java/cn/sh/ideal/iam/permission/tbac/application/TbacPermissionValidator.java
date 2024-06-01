package cn.sh.ideal.iam.permission.tbac.application;

import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantCache;
import cn.sh.ideal.iam.security.api.Authorities;
import cn.sh.ideal.iam.security.api.PermissionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TbacPermissionValidator implements PermissionValidator {
    private final TbacHandler tbacHandler;
    private final TenantCache tenantCache;

    @Override
    public boolean hasAuthority(@Nonnull Long userId,
                                @Nonnull Long tenantId,
                                @Nonnull String authority) {
        Tenant tenant = tenantCache.get(tenantId).orElse(null);
        if (tenant == null) {
            log.info("权限校验返回false, 租户不存在: {}", tenantId);
            return false;
        }
        Long containerId = tenant.getContainerId();
        if (containerId == null) {
            log.info("权限校验返回false, 租户没有分配安全容器: {}", tenantId);
            return false;
        }
        return tbacHandler.hasAuthority(userId, containerId, authority);
    }

    @Override
    public boolean hasApiPermission(long userId, long tenantId,
                                    @Nonnull String method, @Nonnull String path) {
        Tenant tenant = tenantCache.get(tenantId).orElse(null);
        if (tenant == null) {
            log.info("API权限校验返回false, 租户不存在: {}", tenantId);
            return false;
        }
        Long containerId = tenant.getContainerId();
        if (containerId == null) {
            log.info("API权限校验返回false, 租户没有分配安全容器: {}", tenantId);
            return false;
        }

        return tbacHandler.hasApiPermission(userId, containerId, method, path);
    }

    @Override
    public boolean validateTenantAccess(long userId, @Nonnull Long tenantId) {
        Tenant tenant = tenantCache.get(tenantId).orElse(null);
        if (tenant == null) {
            log.info("验证是否有租户访问权限返回false, 租户不存在: {}", tenantId);
            return false;
        }
        Long containerId = tenant.getContainerId();
        if (containerId == null) {
            log.info("验证是否有租户访问权限返回false, 租户没有分配安全容器: {}", tenantId);
            return false;
        }
        return tbacHandler.hasAuthority(userId, containerId, Authorities.TENANT_ACCESS);
    }
}
