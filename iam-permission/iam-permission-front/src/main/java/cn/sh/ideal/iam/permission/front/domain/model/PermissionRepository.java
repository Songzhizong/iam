package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface PermissionRepository {
    List<PermissionRepositoryListener> listeners = new ArrayList<>();

    default void addListener(@Nonnull PermissionRepositoryListener listener) {
        listeners.add(listener);
    }

    @Nonnull
    Permission insert(@Nonnull Permission permission);

    void insert(@Nonnull List<Permission> permissions);

    int deleteAllByAppId(@Nonnull Long appId);

    @Nonnull
    List<Permission> findAll();

    @Nonnull
    List<Permission> findAllByAppId(@Nonnull Long appId);

    @Nonnull
    List<Permission> findAllById(@Nonnull Collection<Long> ids);

    @Nonnull
    List<Permission> findAllByItemIdIn(@Nonnull Collection<Long> itemIds);

    boolean existsByAppId(@Nonnull Long appId);
}
