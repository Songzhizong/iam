package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionGroupRepository {

    @Nonnull
    PermissionGroup insert(@Nonnull PermissionGroup group);

    void insert(@Nonnull List<PermissionGroup> permissionGroups);

    int deleteAllByAppId(long appId);

    @Nonnull
    Optional<PermissionGroup> findById(long id);

    @Nonnull
    List<PermissionGroup> findAllByAppId(long appId);

    boolean existsByAppId(long appId);
}
