package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/15
 */
public interface UserJpaRepository extends JpaRepository<UserDO, Long> {

    boolean existsByTenantIdAndAccount(long tenantId, @Nonnull String account);

    @Nonnull
    Optional<UserDO> findByEmailAndPlatform(@Nonnull String email, @Nonnull String platform);

    @Nonnull
    Optional<UserDO> findByTenantIdAndAccount(long tenantId, @Nonnull String account);
}
