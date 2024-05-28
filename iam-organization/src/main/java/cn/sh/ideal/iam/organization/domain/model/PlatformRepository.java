package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PlatformRepository {

    @Nonnull
    Platform insert(@Nonnull Platform platform);

    @Nonnull
    Platform update(@Nonnull Platform platform);

    @Nonnull
    Optional<Platform> findByCode(@Nonnull String code);

    @Nonnull
    List<Platform> findAll();

    @Nonnull
    Platform requireByCode(@Nonnull String code);
}
