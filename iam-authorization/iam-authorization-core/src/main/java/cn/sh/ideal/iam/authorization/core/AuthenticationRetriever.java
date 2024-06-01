package cn.sh.ideal.iam.authorization.core;

import cn.sh.ideal.iam.security.api.Authentication;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AuthenticationRetriever {

    boolean supports(@Nonnull String authorization);

    @Nonnull
    Authentication retrieve(@Nonnull String authorization);
}
