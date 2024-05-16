package cn.sh.ideal.iam.jdbc.permission.front;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionJpaRepository extends JpaRepository<PermissionDO, Long> {

    @Nonnull
    List<PermissionDO> findAllByAppId(long appId);

    @Modifying
    @Query(value = "DELETE FROM iam_permission AS e WHERE e.app_id_ = :appId", nativeQuery = true)
    long deleteAllByAppId(@Param("appId") long appId);

    boolean existsByAppId(long appId);
}
