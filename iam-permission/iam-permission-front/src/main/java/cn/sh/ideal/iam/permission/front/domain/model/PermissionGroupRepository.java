package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionGroupRepository {

    void insert(@Nonnull List<PermissionGroup> permissionGroups);

    long deleteAllByAppId(long appId);

    @Nonnull
    List<PermissionGroup> findAllByAppId(long appId);

    boolean existsByAppId(long appId);
}
