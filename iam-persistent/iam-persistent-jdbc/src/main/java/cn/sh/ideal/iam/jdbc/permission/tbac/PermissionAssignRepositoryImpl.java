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
public class PermissionAssignRepositoryImpl implements PermissionAssignRepository {
    private final PermissionAssignJpaRepository permissionAssignJpaRepository;

    @Override
    public void insert(@Nonnull List<PermissionAssign> assigns) {
        for (PermissionAssign assign : assigns) {
            PermissionAssignDO entity = (PermissionAssignDO) assign;
            permissionAssignJpaRepository.save(entity);
        }
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
    public void deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(long containerId, long userGroupId, @Nonnull List<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            return;
        }
        permissionAssignJpaRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(containerId, userGroupId, permissionIds);

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
}
