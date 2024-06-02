package cn.sh.ideal.iam.permission.tbac.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/30
 */
public interface PermissionAssignRepository {

    void insert(@Nonnull List<PermissionAssign> assigns);

    int deleteAllByContainerIdAndUserGroupId(@Nonnull Long containerId, @Nonnull Long userGroupId);

    void deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(@Nonnull Long containerId,
                                                                   @Nonnull Long userGroupId,
                                                                   @Nonnull Collection<Long> permissionItemIds);

    int deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(@Nonnull Long containerId,
                                                              @Nonnull Long userGroupId,
                                                              @Nonnull Collection<Long> permissionIds);

    @Nonnull
    List<PermissionAssign> findAllByUserGroupIdIn(@Nonnull Collection<Long> userGroupIds);

    @Nonnull
    List<PermissionAssign> findAllByPermissionIdAndUserGroupIdIn(@Nonnull Long permissionId,
                                                                 @Nonnull Collection<Long> userGroupIds);

    void deleteAllByAppIdAndContainerIdAndUserGroupId(@Nonnull Long appId,
                                                      @Nonnull Long containerId,
                                                      @Nonnull Long userGroupId);
}
