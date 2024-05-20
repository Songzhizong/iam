package cn.sh.ideal.iam.permission.tbac.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionAssignRepository {

    void insert(@Nonnull List<PermissionAssign> assigns);

    void deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(long containerId,
                                                                   long userGroupId,
                                                                   @Nonnull Collection<Long> permissionItemIds);

    void deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(long containerId,
                                                               long userGroupId,
                                                               @Nonnull List<Long> permissionIds);

    @Nonnull
    List<PermissionAssign> findAllByUserGroupIdIn(@Nonnull Collection<Long> userGroupIds);

    void deleteAllByAppIdAndContainerIdAndUserGroupId(long appId, long containerId, long userGroupId);
}
