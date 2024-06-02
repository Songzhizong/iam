package cn.sh.ideal.iam.security.standard;

import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.PermissionValidator;
import cn.sh.ideal.iam.security.api.SecurityContext;
import cn.sh.ideal.iam.security.api.TenantAccessibility;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SecurityContextImpl implements SecurityContext {
    private final Authentication authentication;
    private final PermissionValidator permissionValidator;
    private final TenantAccessibility tenantAccessibility;

    @Nonnull
    @Override
    public Authentication authentication() {
        return authentication;
    }

    @Nonnull
    @Override
    public PermissionValidator permissionValidator() {
        return permissionValidator;
    }

    @Nonnull
    @Override
    public TenantAccessibility tenantAccessibility() {
        return tenantAccessibility;
    }
}
