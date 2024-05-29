package cn.sh.ideal.iam.jdbc.authorization.standard;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
