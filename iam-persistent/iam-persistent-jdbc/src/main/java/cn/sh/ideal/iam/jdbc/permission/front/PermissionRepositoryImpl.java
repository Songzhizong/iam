package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.concurrent.Asyncs;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionRepository;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionRepositoryListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {
    private final PermissionJpaRepository permissionJpaRepository;

    @Nonnull
    @Override
    public Permission insert(@Nonnull Permission permission) {
        PermissionDO entity = (PermissionDO) permission;
        PermissionDO saved = permissionJpaRepository.saveAndFlush(entity);
        Asyncs.executeVirtual(() -> {
            for (PermissionRepositoryListener listener : listeners) {
                listener.onPermissionTableChanged();
            }
        });
        return saved;
    }

    @Override
    public void insert(@Nonnull List<Permission> permissions) {
        for (Permission permission : permissions) {
            PermissionDO entity = (PermissionDO) permission;
            permissionJpaRepository.save(entity);
        }
        permissionJpaRepository.flush();
        Asyncs.delayExec(Duration.ofSeconds(1), () -> {
            for (PermissionRepositoryListener listener : listeners) {
                listener.onPermissionTableChanged();
            }
        });
    }

    @Override
    public int deleteAllByAppId(@Nonnull Long appId) {
        int deleted = permissionJpaRepository.deleteAllByAppId(appId);
        Asyncs.delayExec(Duration.ofSeconds(1), () -> {
            for (PermissionRepositoryListener listener : listeners) {
                listener.onPermissionTableChanged();
            }
        });
        return deleted;
    }

    @Nonnull
    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAll()
                .stream().map(e -> (Permission) e).toList();
    }

    @Nonnull
    @Override
    public List<Permission> findAllByAppId(@Nonnull Long appId) {
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
    public boolean existsByAppId(@Nonnull Long appId) {
        return permissionJpaRepository.existsByAppId(appId);
    }
}
