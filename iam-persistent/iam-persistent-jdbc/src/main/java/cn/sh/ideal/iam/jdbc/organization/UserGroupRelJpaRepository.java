package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/15
 */
public interface UserGroupRelJpaRepository extends JpaRepository<UserGroupRelDO, Long> {

    @Nonnull
    List<UserGroupRelDO> findAllByUserId(long userId);

    @Modifying
    @Transactional(rollbackFor = Throwable.class)
    @Query(value = "DELETE FROM iam_user_group_user_rel AS e WHERE e.user_id_ = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") long userId);

    @Modifying
    @Transactional(rollbackFor = Throwable.class)
    @Query(value = "DELETE FROM iam_user_group_user_rel AS e WHERE e.group_id_ = :groupId", nativeQuery = true)
    void deleteAllByGroupId(@Param("groupId") long groupId);

}
