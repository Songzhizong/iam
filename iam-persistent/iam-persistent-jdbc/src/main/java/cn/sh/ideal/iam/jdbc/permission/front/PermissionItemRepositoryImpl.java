package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.permission.front.domain.model.PermissionItem;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionItemRepository;
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
public class PermissionItemRepositoryImpl implements PermissionItemRepository {
    private final PermissionItemJpaRepository permissionItemJpaRepository;

    @Nonnull
    @Override
    public PermissionItem insert(@Nonnull PermissionItem item) {
        PermissionItemDO entity = (PermissionItemDO) item;
        return permissionItemJpaRepository.save(entity);
    }

    @Override
    public void insert(@Nonnull List<PermissionItem> permissionItems) {
        for (PermissionItem permissionItem : permissionItems) {
            PermissionItemDO entity = (PermissionItemDO) permissionItem;
            permissionItemJpaRepository.save(entity);
        }
    }

    @Override
    public int deleteAllByAppId(long appId) {
        return permissionItemJpaRepository.deleteAllByAppId(appId);
    }

    @Nonnull
    @Override
    public Optional<PermissionItem> findById(long id) {
        return permissionItemJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<PermissionItem> findAll() {
        return permissionItemJpaRepository.findAll()
                .stream().map(e -> (PermissionItem) e).toList();
    }

    @Nonnull
    @Override
    public List<PermissionItem> findAllByAppId(long appId) {
        return permissionItemJpaRepository.findAllByAppId(appId)
                .stream().map(e -> (PermissionItem) e).toList();
    }

    @Override
    public boolean existsByAppId(long appId) {
        return permissionItemJpaRepository.existsByAppId(appId);
    }

}
