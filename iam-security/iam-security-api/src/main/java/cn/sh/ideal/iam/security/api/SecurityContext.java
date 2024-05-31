package cn.sh.ideal.iam.security.api;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface SecurityContext {

    /**
     * 获取当前经过身份验证的主体或身份验证请求令牌。
     */
    @Nonnull
    Authentication authentication();


    /**
     * 获取授予主体的权限验证接口
     *
     * @return 授予主体的权限验证接口
     */
    @Nonnull
    AuthorityValidator authorityValidator();
}
