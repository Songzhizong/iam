package cn.sh.ideal.iam.security.api.adapter;

import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.AuthorityValidator;
import jakarta.servlet.http.HttpServletRequest;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/31
 */
public interface AuthorityValidatorFactory {


    @Nonnull
    AuthorityValidator createAuthorityValidator(@Nonnull Authentication authentication,
                                                @Nonnull HttpServletRequest httpServletRequest);
}
