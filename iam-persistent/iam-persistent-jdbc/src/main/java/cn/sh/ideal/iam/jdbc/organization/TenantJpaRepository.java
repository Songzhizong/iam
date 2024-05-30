package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface TenantJpaRepository extends JpaRepository<TenantDO, Long> {

    boolean existsByContainerId(long containerId);

    boolean existsByAbbreviation(@Nonnull String abbreviation);

    boolean existsByPlatformAndAbbreviation(@Nonnull String platform,
                                            @Nonnull String abbreviation);

    @Nonnull
    Optional<TenantDO> findByPlatformAndAbbreviation(@Nonnull String platform,
                                                     @Nonnull String abbreviation);
}
