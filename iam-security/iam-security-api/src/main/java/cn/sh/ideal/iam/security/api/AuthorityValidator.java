package cn.sh.ideal.iam.security.api;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AuthorityValidator {

    /**
     * 判断是否拥有指定的权限
     *
     * @param authority 权限字符串
     * @return 是否拥有指定的权限
     */
    boolean hasAuthority(@Nonnull String authority);

    /**
     * 判断用户是否拥有API接口的访问权限
     *
     * @param method http请求方法
     * @param path   请求路径
     * @return 是否拥有接口权限
     * @author 宋志宗 on 2024/5/18
     */
    boolean hasApiPermission(@Nonnull String method, @Nonnull String path);
}
