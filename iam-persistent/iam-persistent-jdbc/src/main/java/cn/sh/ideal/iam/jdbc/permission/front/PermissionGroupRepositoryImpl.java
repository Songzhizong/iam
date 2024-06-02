package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.permission.front.domain.model.PermissionGroup;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Repository
@RequiredArgsConstructor
public class PermissionGroupRepositoryImpl implements PermissionGroupRepository {
    private final PermissionGroupJpaRepository permissionGroupJpaRepository;

    @Nonnull
    @Override
    public PermissionGroup insert(@Nonnull PermissionGroup group) {
        PermissionGroupDO entity = (PermissionGroupDO) group;
        return permissionGroupJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void insert(@Nonnull List<PermissionGroup> permissionGroups) {
        for (PermissionGroup permissionGroup : permissionGroups) {
            PermissionGroupDO entity = (PermissionGroupDO) permissionGroup;
            permissionGroupJpaRepository.save(entity);
        }
        permissionGroupJpaRepository.flush();
    }

    @Override
    public int deleteAllByAppId(@Nonnull Long appId) {
        return permissionGroupJpaRepository.deleteAllByAppId(appId);
    }

    @Nonnull
    @Override
    public Optional<PermissionGroup> findById(@Nonnull Long id) {
        return permissionGroupJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<PermissionGroup> findAll() {
        return permissionGroupJpaRepository.findAll()
                .stream().map(e -> (PermissionGroup) e).toList();
    }

    @Nonnull
    @Override
    public List<PermissionGroup> findAllByAppId(@Nonnull Long appId) {
        return permissionGroupJpaRepository.findAllByAppId(appId)
                .stream().map(e -> (PermissionGroup) e).toList();
    }

    @Override
    public boolean existsByAppId(@Nonnull Long appId) {
        return permissionGroupJpaRepository.existsByAppId(appId);
    }
}
