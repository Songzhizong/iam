package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionRepository {

    @Nonnull
    Permission insert(@Nonnull Permission permission);

    void insert(@Nonnull List<Permission> permissions);

    int deleteAllByAppId(long appId);

    @Nonnull
    List<Permission> findAllByAppId(long appId);

    boolean existsByAppId(long appId);
}
