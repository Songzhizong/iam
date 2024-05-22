package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.tbac.configure.TbacI18nReader;
import cn.sh.ideal.iam.permission.tbac.dto.args.CreateSecurityContainerArgs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface EntityFactory {

    @Nonnull
    default SecurityContainer securityContainer(@Nonnull CreateSecurityContainerArgs args,
                                                @Nonnull TbacI18nReader i18nReader) {
        return securityContainer(null, args, i18nReader);
    }

    @Nonnull
    SecurityContainer securityContainer(@Nullable SecurityContainer parent,
                                        @Nonnull CreateSecurityContainerArgs args,
                                        @Nonnull TbacI18nReader i18nReader);

    @Nonnull
    List<PermissionAssign> assignPermissions(long containerId,
                                             long userGroupId,
                                             boolean assign,
                                             boolean inheritable,
                                             boolean mfa,
                                             @Nonnull List<Permission> permissions);

    @Nonnull
    PermissionAssign assignPermission(long containerId,
                                       long userGroupId,
                                       boolean assign,
                                       boolean inheritable,
                                       boolean mfa,
                                       @Nonnull Permission permission);
}
