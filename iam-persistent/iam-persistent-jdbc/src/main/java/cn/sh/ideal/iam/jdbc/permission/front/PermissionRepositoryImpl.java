package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {
    private final PermissionJpaRepository permissionJpaRepository;

    @Nonnull
    @Override
    public Permission insert(@Nonnull Permission permission) {
        PermissionDO entity = (PermissionDO) permission;
        return permissionJpaRepository.save(entity);
    }

    @Override
    public void insert(@Nonnull List<Permission> permissions) {
        for (Permission permission : permissions) {
            PermissionDO entity = (PermissionDO) permission;
            permissionJpaRepository.save(entity);
        }
    }

    @Override
    public int deleteAllByAppId(long appId) {
        return permissionJpaRepository.deleteAllByAppId(appId);
    }

    @Nonnull
    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAll()
                .stream().map(e -> (Permission) e).toList();
    }

    @Nonnull
    @Override
    public List<Permission> findAllByAppId(long appId) {
        return permissionJpaRepository.findAllByAppId(appId)
                .stream().map(e -> (Permission) e).toList();
    }

    @Nonnull
    @Override
    public List<Permission> findAllById(@Nonnull Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return permissionJpaRepository.findAllById(ids)
                .stream().map(e -> (Permission) e).toList();

    }

    @Nonnull
    @Override
    public List<Permission> findAllByItemIdIn(@Nonnull Collection<Long> itemIds) {
        if (itemIds.isEmpty()) {
            return List.of();
        }
        return permissionJpaRepository.findAllByItemIdIn(itemIds)
                .stream().map(e -> (Permission) e).toList();

    }

    @Override
    public boolean existsByAppId(long appId) {
        return permissionJpaRepository.existsByAppId(appId);
    }

    @Override
    public boolean existsByUpdatedTimeGte(long updatedTimeGte) {
        return permissionJpaRepository.existsByUpdatedTimeGreaterThanEqual(updatedTimeGte);
    }
}
