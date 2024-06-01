package cn.sh.ideal.iam.permission.rbac.application;

import cn.sh.ideal.iam.security.api.PermissionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RbacPermissionValidator implements PermissionValidator {
    private final RbacHandler rbacHandler;

    @Override
    public boolean hasAuthority(@Nonnull Long userId,
                                @Nonnull Long tenantId,
                                @Nonnull String authority) {
        return rbacHandler.hasAuthority(userId, tenantId, authority);
    }

    @Override
    public boolean hasApiPermission(long userId, long tenantId,
                                    @Nonnull String method, @Nonnull String path) {
        return rbacHandler.hasApiPermission(userId, tenantId, method, path);
    }

    @Override
    public boolean validateTenantAccess(long userId, @Nonnull Long tenantId) {
        return false;
    }
}
