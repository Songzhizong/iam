package cn.sh.ideal.iam.security.api;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/2
 */
public class PermitAllPermissionValidator implements PermissionValidator {
    private static final PermitAllPermissionValidator INSTANCE = new PermitAllPermissionValidator();

    public static PermitAllPermissionValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean hasAuthority(@Nonnull String authority) {
        return true;
    }

    @Override
    public boolean hasApiPermission(@Nonnull String method, @Nonnull String path) {
        return true;
    }
}
