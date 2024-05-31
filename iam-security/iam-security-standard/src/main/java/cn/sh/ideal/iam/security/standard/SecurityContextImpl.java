package cn.sh.ideal.iam.security.standard;

import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.AuthorityValidator;
import cn.sh.ideal.iam.security.api.SecurityContext;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class SecurityContextImpl implements SecurityContext {
    private final Authentication authentication;

    @Nonnull
    @Override
    public Authentication authentication() {
        return authentication;
    }

    @Nonnull
    @Override
    public AuthorityValidator authorityValidator() {
        return null;
    }
}
