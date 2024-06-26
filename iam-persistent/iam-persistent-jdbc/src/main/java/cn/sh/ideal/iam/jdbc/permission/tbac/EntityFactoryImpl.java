package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.tbac.domain.model.EntityFactory;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import cn.sh.ideal.iam.permission.tbac.domain.model.SecurityContainer;
import cn.sh.ideal.iam.permission.tbac.dto.args.CreateSecurityContainerArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Component("tbacEntityFactory")
public class EntityFactoryImpl implements EntityFactory {

    @Nonnull
    @Override
    public SecurityContainer securityContainer(@Nullable SecurityContainer parent,
                                               @Nonnull CreateSecurityContainerArgs args,
                                               @Nonnull IamI18nReader i18nReader) {
        return SecurityContainerDO.create(parent, args, i18nReader);
    }

    @Nonnull
    @Override
    public List<PermissionAssign> assignPermissions(@Nonnull Long containerId,
                                                    @Nonnull Long userGroupId,
                                                    boolean assign,
                                                    boolean inheritable,
                                                    boolean mfa,
                                                    @Nonnull List<Permission> permissions) {
        List<PermissionAssign> assigns = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionAssign permissionAssign = TbacPermissionAssignDO.create(
                    containerId, userGroupId, assign, inheritable, mfa, permission);
            assigns.add(permissionAssign);
        }
        return assigns;
    }

    @Nonnull
    @Override
    public PermissionAssign assignPermission(@Nonnull Long containerId,
                                             @Nonnull Long userGroupId,
                                             boolean assign,
                                             boolean inheritable,
                                             boolean mfa,
                                             @Nonnull Permission permission) {
        return TbacPermissionAssignDO.create(containerId, userGroupId, assign, inheritable, mfa, permission);
    }

}
