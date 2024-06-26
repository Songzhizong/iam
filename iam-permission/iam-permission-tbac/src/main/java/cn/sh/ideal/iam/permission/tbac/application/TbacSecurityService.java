package cn.sh.ideal.iam.permission.tbac.application;

import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantCache;
import cn.sh.ideal.iam.security.api.AccessibleTenant;
import cn.sh.ideal.iam.security.api.AuthorityConstants;
import cn.sh.ideal.iam.security.api.adapter.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.SequencedCollection;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TbacSecurityService implements SecurityService {
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
    public boolean hasApiPermission(@Nonnull Long userId, @Nonnull Long tenantId,
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
    public boolean isTenantAccessible(@Nonnull Long userId, @Nonnull Long tenantId) {
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
        return tbacHandler.hasAuthority(userId, containerId, AuthorityConstants.TENANT_ACCESS);
    }

    @Nonnull
    @Override
    public SequencedCollection<AccessibleTenant> accessibleTenants(@Nonnull Long userId) {
        return tbacHandler.accessibleTenants(userId);
    }
}
