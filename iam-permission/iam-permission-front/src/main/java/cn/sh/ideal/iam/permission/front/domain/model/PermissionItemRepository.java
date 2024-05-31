package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionItemRepository {

    @Nonnull
    PermissionItem insert(@Nonnull PermissionItem item);

    void insert(@Nonnull List<PermissionItem> permissionItems);

    int deleteAllByAppId(long appId);

    @Nonnull
    Optional<PermissionItem> findById(long id);

    @Nonnull
    List<PermissionItem> findAll();

    @Nonnull
    List<PermissionItem> findAllByAppId(long appId);

    boolean existsByAppId(long appId);
}
