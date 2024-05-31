package cn.sh.ideal.iam.authorization.core;

import cn.sh.ideal.iam.security.api.Authentication;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AuthorizationAnalyzer {

    boolean supports(@Nonnull String authorization);

    Authentication analyze(@Nonnull String authorization);
}
