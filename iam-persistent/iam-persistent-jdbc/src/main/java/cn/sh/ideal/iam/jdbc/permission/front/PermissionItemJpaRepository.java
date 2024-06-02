package cn.sh.ideal.iam.jdbc.permission.front;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface PermissionItemJpaRepository extends JpaRepository<PermissionItemDO, Long> {

    @Nonnull
    List<PermissionItemDO> findAllByAppId(long appId);

    @Modifying
    @Transactional(rollbackFor = Throwable.class)
    @Query(value = "DELETE FROM iam_permission_item AS e WHERE e.app_id_ = :appId", nativeQuery = true)
    int deleteAllByAppId(@Param("appId") @Nonnull Long appId);

    boolean existsByAppId(@Nonnull Long appId);
}
