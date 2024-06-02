package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Repository
@RequiredArgsConstructor
public class TbacPermissionAssignRepositoryImpl implements PermissionAssignRepository {
    private final TbacPermissionAssignJpaRepository permissionAssignJpaRepository;

    @Override
    public void insert(@Nonnull List<PermissionAssign> assigns) {
        if (assigns.isEmpty()) {
            return;
        }
        for (PermissionAssign assign : assigns) {
            TbacPermissionAssignDO entity = (TbacPermissionAssignDO) assign;
            permissionAssignJpaRepository.save(entity);
        }
    }

    @Override
    public int deleteAllByContainerIdAndUserGroupId(@Nonnull Long containerId,
                                                    @Nonnull Long userGroupId) {
        return permissionAssignJpaRepository.deleteAllByContainerIdAndUserGroupId(containerId, userGroupId);
    }

    @Override
    public void deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(@Nonnull Long containerId,
                                                                          @Nonnull Long userGroupId,
                                                                          @Nonnull Collection<Long> permissionItemIds) {
        if (permissionItemIds.isEmpty()) {
            return;
        }
        permissionAssignJpaRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(containerId, userGroupId, permissionItemIds);
    }

    @Override
    public int deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(@Nonnull Long containerId,
                                                                     @Nonnull Long userGroupId,
                                                                     @Nonnull Collection<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            return 0;
        }
        return permissionAssignJpaRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(containerId, userGroupId, permissionIds);
    }

    @Nonnull
    @Override
    public List<PermissionAssign> findAllByUserGroupIdIn(@Nonnull Collection<Long> userGroupIds) {
        if (userGroupIds.isEmpty()) {
            return List.of();
        }
        return permissionAssignJpaRepository.findAllByUserGroupIdIn(userGroupIds)
                .stream().map(e -> (PermissionAssign) e).toList();
    }

    @Nonnull
    @Override
    public List<PermissionAssign> findAllByPermissionIdAndUserGroupIdIn(@Nonnull Long permissionId,
                                                                        @Nonnull Collection<Long> userGroupIds) {
        if (userGroupIds.isEmpty()) {
            return List.of();
        }
        return permissionAssignJpaRepository
                .findAllByPermissionIdAndUserGroupIdIn(permissionId, userGroupIds)
                .stream().map(e -> (PermissionAssign) e).toList();
    }

    @Override
    public void deleteAllByAppIdAndContainerIdAndUserGroupId(@Nonnull Long appId,
                                                             @Nonnull Long containerId,
                                                             @Nonnull Long userGroupId) {
        permissionAssignJpaRepository.deleteAllByAppIdAndContainerIdAndUserGroupId(appId, containerId, userGroupId);
    }
}
