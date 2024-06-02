package cn.sh.ideal.iam.jdbc.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface PlatformJpaRepository extends JpaRepository<PlatformDO, String> {

    @Nonnull
    Optional<PlatformDO> findByCode(@Nonnull String code);

    @Nonnull
    List<PlatformDO> findAllByDeleted(boolean deleted);

    boolean existsByIdGreaterThan(long idGt);

    void deleteByCode(@Nonnull String code);
}
