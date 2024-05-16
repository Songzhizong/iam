package cn.sh.ideal.iam.permission.front.application;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
import cn.sh.ideal.iam.permission.front.domain.model.*;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionGroupArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionItemArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final IamIDGenerator idGenerator;
    private final EntityFactory entityFactory;
    private final PermissionRepository permissionRepository;
    private final PermissionItemRepository permissionItemRepository;
    private final PermissionGroupRepository permissionGroupRepository;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public PermissionGroup createGroup(@Nonnull CreatePermissionGroupArgs args) {
        long id = idGenerator.generate();
        PermissionGroup group = entityFactory.permissionGroup(id, args);
        return permissionGroupRepository.insert(group);
    }


    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public PermissionItem createItem(@Nonnull CreatePermissionItemArgs args) {
        Long groupId = args.getGroupId();
        Asserts.nonnull(groupId, "权限配置组id为空");
        PermissionGroup group = permissionGroupRepository.findById(groupId).orElseThrow(() -> {
            log.info("新增权限项失败, 权限组[{}]不存在", groupId);
            return new ResourceNotFoundException("权限组不存在");
        });
        long id = idGenerator.generate();
        PermissionItem item = entityFactory.permissionItem(id, group, args);
        return permissionItemRepository.insert(item);
    }

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public Permission createPermission(@Nonnull CreatePermissionArgs args) {
        Long itemId = args.getItemId();
        Asserts.nonnull(itemId, "权限配置项ID为空");
        PermissionItem item = permissionItemRepository.findById(itemId).orElseThrow(() -> {
            log.info("新增权限失败, 权限配置项[{}]不存在", itemId);
            return new ResourceNotFoundException("权限配置项不存在");
        });
        long id = idGenerator.generate();
        Permission permission = entityFactory.permission(id, item, args);
        return permissionRepository.insert(permission);
    }

}
