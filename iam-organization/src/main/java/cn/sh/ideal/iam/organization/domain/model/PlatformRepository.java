package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface PlatformRepository {
    List<PlatformRepositoryListener> listeners = new ArrayList<>();

    default void addListener(@Nonnull PlatformRepositoryListener listener) {
        listeners.add(listener);
    }

    @Nonnull
    Platform insert(@Nonnull Platform platform);

    @Nonnull
    Platform update(@Nonnull Platform platform);

    void delete(@Nonnull Platform platform);

    @Nonnull
    Optional<Platform> findByCode(@Nonnull String code);

    @Nonnull
    List<Platform> findAll();

    @Nonnull
    Platform requireByCode(@Nonnull String code);

    boolean exists();
}
