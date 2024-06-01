package cn.sh.ideal.iam.permission.rbac.application;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/1
 */
public interface RbacHandler {

    boolean hasAuthority(long userId, long tenantId, @Nonnull String authority);

    boolean hasApiPermission(long userId, long tenantId,
                             @Nonnull String method, @Nonnull String path);
}
