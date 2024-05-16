package cn.sh.ideal.iam.jdbc.permission.front;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionGroupJpaRepository extends JpaRepository<PermissionGroupDO, Long> {

    @Nonnull
    List<PermissionGroupDO> findAllByAppId(long appId);

    @Modifying
    @Query(value = "DELETE FROM iam_permission_group AS e WHERE e.app_id_ = :appId", nativeQuery = true)
    long deleteAllByAppId(long appId);

    boolean existsByAppId(long appId);
}
