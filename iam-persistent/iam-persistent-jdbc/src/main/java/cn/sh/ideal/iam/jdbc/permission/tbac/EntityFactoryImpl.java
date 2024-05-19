package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.tbac.domain.model.EntityFactory;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Component("tbacEntityFactory")
public class EntityFactoryImpl implements EntityFactory {

    @Override
    public List<PermissionAssign> assignPermissions(long containerId,
                                                    long userGroupId,
                                                    boolean assign,
                                                    boolean inheritable,
                                                    boolean mfa,
                                                    @Nonnull List<Permission> permissions) {
        List<PermissionAssign> assigns = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionAssign permissionAssign = PermissionAssignDO.create(
                    containerId, userGroupId, assign, inheritable, mfa, permission);
            assigns.add(permissionAssign);
        }
        return assigns;
    }
}
