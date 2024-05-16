package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionItemRepository {

    void insert(@Nonnull List<PermissionItem> permissionItems);

    long deleteAllByAppId(long appId);

    @Nonnull
    List<PermissionItem> findAllByAppId(long appId);

    boolean existsByAppId(long appId);
}
