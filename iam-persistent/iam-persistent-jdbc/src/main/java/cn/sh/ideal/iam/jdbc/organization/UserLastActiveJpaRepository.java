package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface UserLastActiveJpaRepository extends JpaRepository<UserLastActiveDO, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE iam_user_last_active
            SET last_active_time_ = :lastActiveTime
            WHERE id_=:id""", nativeQuery = true)
    void updateLastActiveTimeById(@Nonnull @Param("id") Long id,
                                  @Param("lastActiveTime") long lastActiveTime);
}
