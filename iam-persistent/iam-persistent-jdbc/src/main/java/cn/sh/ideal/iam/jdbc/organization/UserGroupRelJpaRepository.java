package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/15
 */
public interface UserGroupRelJpaRepository extends JpaRepository<UserGroupRelDO, Long> {

    @Nonnull
    List<UserGroupRelDO> findAllByUserId(long userId);

    @Modifying
    @Query(value = "delete from iam_user_group_user_rel as e where e.user_id_ = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") long userId);

    @Modifying
    @Query(value = "delete from iam_user_group_user_rel as e where e.group_id_ = :groupId", nativeQuery = true)
    void deleteAllByGroupId(@Param("groupId") long groupId);

}
