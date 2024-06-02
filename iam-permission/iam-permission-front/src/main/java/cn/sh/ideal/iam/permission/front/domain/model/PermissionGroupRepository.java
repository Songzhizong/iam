package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface PermissionGroupRepository {

    @Nonnull
    PermissionGroup insert(@Nonnull PermissionGroup group);

    void insert(@Nonnull List<PermissionGroup> permissionGroups);

    int deleteAllByAppId(@Nonnull Long appId);

    @Nonnull
    Optional<PermissionGroup> findById(@Nonnull Long id);

    @Nonnull
    List<PermissionGroup> findAll();

    @Nonnull
    List<PermissionGroup> findAllByAppId(@Nonnull Long appId);

    boolean existsByAppId(@Nonnull Long appId);
}
