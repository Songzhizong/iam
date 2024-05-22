package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
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
    public int deleteAllByContainerIdAndUserGroupId(long containerId, long userGroupId) {
        return permissionAssignJpaRepository.deleteAllByContainerIdAndUserGroupId(containerId, userGroupId);
    }

    @Override
    public void deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(long containerId,
                                                                          long userGroupId,
                                                                          @Nonnull Collection<Long> permissionItemIds) {
        if (permissionItemIds.isEmpty()) {
            return;
        }
        permissionAssignJpaRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(containerId, userGroupId, permissionItemIds);
    }

    @Override
    public int deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(long containerId, long userGroupId, @Nonnull Collection<Long> permissionIds) {
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

    @Override
    public void deleteAllByAppIdAndContainerIdAndUserGroupId(long appId, long containerId, long userGroupId) {
        permissionAssignJpaRepository.deleteAllByAppIdAndContainerIdAndUserGroupId(appId, containerId, userGroupId);
    }
}
