package cn.sh.ideal.iam.security.api;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/1
 */
public interface PermissionValidator {

    boolean hasAuthority(@Nonnull Long userId,
                         @Nonnull Long tenantId,
                         @Nonnull String authority);

    boolean hasApiPermission(long userId, long tenantId,
                             @Nonnull String method, @Nonnull String path);

    boolean validateTenantAccess(long userId, @Nonnull Long tenantId);
}
