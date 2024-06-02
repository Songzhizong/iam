package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.tbac.dto.args.CreateSecurityContainerArgs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface EntityFactory {

    @Nonnull
    default SecurityContainer securityContainer(@Nonnull CreateSecurityContainerArgs args,
                                                @Nonnull IamI18nReader i18nReader) {
        return securityContainer(null, args, i18nReader);
    }

    @Nonnull
    SecurityContainer securityContainer(@Nullable SecurityContainer parent,
                                        @Nonnull CreateSecurityContainerArgs args,
                                        @Nonnull IamI18nReader i18nReader);

    @Nonnull
    List<PermissionAssign> assignPermissions(@Nonnull Long containerId,
                                             @Nonnull Long userGroupId,
                                             boolean assign,
                                             boolean inheritable,
                                             boolean mfa,
                                             @Nonnull List<Permission> permissions);

    @Nonnull
    PermissionAssign assignPermission(@Nonnull Long containerId,
                                      @Nonnull Long userGroupId,
                                      boolean assign,
                                      boolean inheritable,
                                      boolean mfa,
                                      @Nonnull Permission permission);
}
