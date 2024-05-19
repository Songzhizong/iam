package cn.sh.ideal.iam.jdbc.permission.tbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionAssignJpaRepository extends JpaRepository<PermissionAssignDO, Long> {


    @Modifying
    @Transactional(rollbackFor = Throwable.class)
    @Query(value = """
            DELETE FROM iam_tbac_permission_assign AS e
                   WHERE e.container_id_ = :containerId
                     AND e.user_group_id_ = :userGroupId
                     AND e.permission_item_id_ in (:permissionItemIds)
            """, nativeQuery = true)
    void deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(@Param("containerId") long containerId,
                                                                   @Param("userGroupId") long userGroupId,
                                                                   @Param("permissionItemIds")
                                                                   @Nonnull Collection<Long> permissionItemIds);

    @Modifying
    @Transactional(rollbackFor = Throwable.class)
    @Query(value = """
            DELETE FROM iam_tbac_permission_assign AS e
                   WHERE e.container_id_ = :containerId
                     AND e.user_group_id_ = :userGroupId
                     AND e.permission_id_ in (:permissionIds)
            """, nativeQuery = true)
    void deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(@Param("containerId") long containerId,
                                                               @Param("userGroupId") long userGroupId,
                                                               @Param("permissionIds")
                                                               @Nonnull List<Long> permissionIds);

    @Nonnull
    List<PermissionAssignDO> findAllByUserGroupIdIn(@Nonnull Collection<Long> userGroupIds);
}
