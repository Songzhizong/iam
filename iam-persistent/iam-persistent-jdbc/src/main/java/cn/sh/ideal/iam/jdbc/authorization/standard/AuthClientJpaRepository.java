package cn.sh.ideal.iam.jdbc.authorization.standard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/30
 */
public interface AuthClientJpaRepository extends JpaRepository<AuthClientDO, Long> {

    @Nonnull
    Optional<AuthClientDO> findByToken(@Nonnull String token);

    List<AuthClientDO> findAllByPlatform(@Nonnull String platform);

    @Modifying
    @Transactional(rollbackFor = Throwable.class)
    @Query(value = "DELETE FROM iam_auth_client AS e WHERE e.platform_ = :platform", nativeQuery = true)
    int deleteAllByPlatform(@Nonnull @Param("platform") String platform);
}
