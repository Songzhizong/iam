package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.sh.ideal.iam.permission.front.domain.model.Permission;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface EntityFactory {

    List<PermissionAssign> assignPermissions(long containerId,
                                             long userGroupId,
                                             boolean assign,
                                             boolean extend,
                                             boolean mfa,
                                             @Nonnull List<Permission> permissions);
}
