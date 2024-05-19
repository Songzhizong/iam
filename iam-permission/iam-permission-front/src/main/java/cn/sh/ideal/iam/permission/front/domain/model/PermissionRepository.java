package cn.sh.ideal.iam.permission.front.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
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
    List<Permission> findAll();

    @Nonnull
    List<Permission> findAllByAppId(long appId);

    @Nonnull
    List<Permission> findAllById(@Nonnull Collection<Long> ids);

    @Nonnull
    List<Permission> findAllByItemIdIn(@Nonnull Collection<Long> itemIds);

    boolean existsByAppId(long appId);

    boolean existsByUpdatedTimeGte(long updatedTimeGte);
}
