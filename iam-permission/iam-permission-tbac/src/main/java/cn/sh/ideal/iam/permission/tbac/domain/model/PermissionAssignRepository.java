package cn.sh.ideal.iam.permission.tbac.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionAssignRepository {

    void insert(@Nonnull List<PermissionAssign> assigns);

    int deleteAllByContainerIdAndUserGroupId(long containerId, long userGroupId);

    void deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(long containerId,
                                                                   long userGroupId,
                                                                   @Nonnull Collection<Long> permissionItemIds);

    int deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(long containerId,
                                                               long userGroupId,
                                                              @Nonnull Collection<Long> permissionIds);

    @Nonnull
    List<PermissionAssign> findAllByUserGroupIdIn(@Nonnull Collection<Long> userGroupIds);

    @Nonnull
    List<PermissionAssign> findAllByPermissionIdAndUserGroupIdIn(long permissionId,
                                                                 @Nonnull Collection<Long> userGroupIds);

    void deleteAllByAppIdAndContainerIdAndUserGroupId(long appId, long containerId, long userGroupId);
}
