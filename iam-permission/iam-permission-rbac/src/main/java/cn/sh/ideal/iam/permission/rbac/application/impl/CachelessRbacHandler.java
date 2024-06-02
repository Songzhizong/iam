package cn.sh.ideal.iam.permission.rbac.application.impl;

import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantCache;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.domain.model.UserCache;
import cn.sh.ideal.iam.permission.rbac.application.RbacHandler;
import cn.sh.ideal.iam.security.api.AccessibleTenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.SequencedCollection;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CachelessRbacHandler implements RbacHandler {
    private final UserCache userCache;
    private final TenantCache tenantCache;

    @Override
    public boolean hasAuthority(@Nonnull Long userId,
                                @Nonnull Long tenantId,
                                @Nonnull String authority) {
        return false;
    }

    @Override
    public boolean hasApiPermission(@Nonnull Long userId, @Nonnull Long tenantId,
                                    @Nonnull String method, @Nonnull String path) {
        return false;
    }

    @Nonnull
    @Override
    public SequencedCollection<AccessibleTenant> accessibleTenants(@Nonnull Long userId) {
        User user = userCache.require(userId);
        Long tenantId = user.getTenantId();
        Tenant tenant = tenantCache.get(tenantId).orElse(null);
        if (tenant == null) {
            return List.of();
        }
        AccessibleTenant accessibleTenant = tenant.toAccessibleTenant();
        return List.of(accessibleTenant);
    }
}
