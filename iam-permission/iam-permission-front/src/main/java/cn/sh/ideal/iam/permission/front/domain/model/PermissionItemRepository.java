package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface PermissionItemRepository {

    @Nonnull
    PermissionItem insert(@Nonnull PermissionItem item);

    void insert(@Nonnull List<PermissionItem> permissionItems);

    int deleteAllByAppId(@Nonnull Long appId);

    @Nonnull
    Optional<PermissionItem> findById(@Nonnull Long id);

    @Nonnull
    List<PermissionItem> findAll();

    @Nonnull
    List<PermissionItem> findAllByAppId(@Nonnull Long appId);

    boolean existsByAppId(@Nonnull Long appId);
}
