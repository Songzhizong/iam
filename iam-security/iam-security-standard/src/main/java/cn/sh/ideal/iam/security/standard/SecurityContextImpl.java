package cn.sh.ideal.iam.security.standard;

import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.AuthorityValidator;
import cn.sh.ideal.iam.security.api.SecurityContext;
import cn.sh.ideal.iam.security.api.TenantAccessibility;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SecurityContextImpl implements SecurityContext {
    private final Authentication authentication;
    private final AuthorityValidator authorityValidator;
    private final TenantAccessibility tenantAccessibility;

    @Nonnull
    @Override
    public Authentication authentication() {
        return authentication;
    }

    @Nonnull
    @Override
    public AuthorityValidator authorityValidator() {
        return authorityValidator;
    }

    @Nonnull
    @Override
    public TenantAccessibility tenantAccessibility() {
        return tenantAccessibility;
    }
}
