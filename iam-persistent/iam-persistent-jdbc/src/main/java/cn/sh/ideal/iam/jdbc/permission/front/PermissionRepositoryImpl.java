package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {
    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public void insert(@Nonnull List<Permission> permissions) {
        for (Permission permission : permissions) {
            PermissionDO entity = (PermissionDO) permission;
            permissionJpaRepository.save(entity);
        }
    }

    @Override
    public long deleteAllByAppId(long appId) {
        return permissionJpaRepository.deleteAllByAppId(appId);
    }

    @Nonnull
    @Override
    public List<Permission> findAllByAppId(long appId) {
        return permissionJpaRepository.findAllByAppId(appId)
                .stream().map(e -> (Permission) e).toList();
    }

    @Override
    public boolean existsByAppId(long appId) {
        return permissionJpaRepository.existsByAppId(appId);
    }
}
