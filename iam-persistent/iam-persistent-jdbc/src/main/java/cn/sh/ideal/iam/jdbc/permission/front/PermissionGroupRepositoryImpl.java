package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.permission.front.domain.model.PermissionGroup;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Repository
@RequiredArgsConstructor
public class PermissionGroupRepositoryImpl implements PermissionGroupRepository {
    private final PermissionGroupJpaRepository permissionGroupJpaRepository;

    @Nonnull
    @Override
    public PermissionGroup insert(@Nonnull PermissionGroup group) {
        PermissionGroupDO entity = (PermissionGroupDO) group;
        return permissionGroupJpaRepository.save(entity);
    }

    @Override
    public void insert(@Nonnull List<PermissionGroup> permissionGroups) {
        for (PermissionGroup permissionGroup : permissionGroups) {
            PermissionGroupDO entity = (PermissionGroupDO) permissionGroup;
            permissionGroupJpaRepository.save(entity);
        }
    }

    @Override
    public int deleteAllByAppId(long appId) {
        return permissionGroupJpaRepository.deleteAllByAppId(appId);
    }

    @Nonnull
    @Override
    public Optional<PermissionGroup> findById(long id) {
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
    public List<PermissionGroup> findAllByAppId(long appId) {
        return permissionGroupJpaRepository.findAllByAppId(appId)
                .stream().map(e -> (PermissionGroup) e).toList();
    }

    @Override
    public boolean existsByAppId(long appId) {
        return permissionGroupJpaRepository.existsByAppId(appId);
    }
}
