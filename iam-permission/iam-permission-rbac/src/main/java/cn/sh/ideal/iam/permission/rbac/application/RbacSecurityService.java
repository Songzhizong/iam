package cn.sh.ideal.iam.permission.rbac.application;

import cn.sh.ideal.iam.security.api.AccessibleTenant;
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
public class RbacSecurityService implements SecurityService {
    private final RbacHandler rbacHandler;

    @Override
    public boolean hasAuthority(@Nonnull Long userId,
                                @Nonnull Long tenantId,
                                @Nonnull String authority) {
        return rbacHandler.hasAuthority(userId, tenantId, authority);
    }

    @Override
    public boolean hasApiPermission(@Nonnull Long userId, @Nonnull Long tenantId,
                                    @Nonnull String method, @Nonnull String path) {
        return rbacHandler.hasApiPermission(userId, tenantId, method, path);
    }

    @Override
    public boolean isTenantAccessible(@Nonnull Long userId, @Nonnull Long tenantId) {
        return false;
    }

    @Nonnull
    @Override
    public SequencedCollection<AccessibleTenant> accessibleTenants(@Nonnull Long userId) {
        return rbacHandler.accessibleTenants(userId);
    }
}
