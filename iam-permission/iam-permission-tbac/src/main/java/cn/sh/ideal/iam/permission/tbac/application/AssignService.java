package cn.sh.ideal.iam.permission.tbac.application;

import cn.idealio.framework.lang.Sets;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.tbac.configure.TbacI18nReader;
import cn.sh.ideal.iam.permission.tbac.domain.model.EntityFactory;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignRepository;
import cn.sh.ideal.iam.permission.tbac.dto.args.AssignPermissionArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignService {
    private final TbacI18nReader i18nReader;
    private final EntityFactory entityFactory;
    private final PermissionCache permissionCache;
    private final PermissionAssignRepository permissionAssignRepository;

    @Transactional(rollbackFor = Throwable.class)
    public void assign(@Nonnull AssignPermissionArgs args) {
        Long containerId = args.getContainerId();
        Long userGroupId = args.getUserGroupId();
        Set<Long> permissionIds = args.getPermissionIds();
        boolean mfa = Objects.requireNonNullElse(args.getMfa(), false);
        boolean assign = Objects.requireNonNullElse(args.getAssign(), true);
        boolean inheritable = Objects.requireNonNullElse(args.getInheritable(), false);
        Asserts.nonnull(containerId, () -> i18nReader.getMessage("container_id.required"));
        Asserts.nonnull(userGroupId, () -> i18nReader.getMessage("user_group_id.required"));
        Asserts.notEmpty(permissionIds, () -> i18nReader.getMessage("permission_ids.required"));

        List<Permission> permissions = permissionCache.findAllById(permissionIds);
        // 记录需要分配所有权限的权限项ID列表
        Set<Long> allPermissionItemIds = new HashSet<>();
        for (Permission permission : permissions) {
            if (permission.isAllInItem()) {
                long itemId = permission.getItemId();
                allPermissionItemIds.add(itemId);
            }
        }

        // 如果没有需要分配所有权限的权限项, 则直接使用传入的权限列表
        List<Permission> assignPermissions = permissions;
        // 如果有权限项需要分配所有权限, 则先删除该容器下该用户组下对应的权限项的所有权限, 再分配所有权限
        // 此外需要从传入的权限列表中过滤掉权限项ID在allPermissionItemIds集合中的权限
        // 最终合并出来的就是所有需要分配的权限
        if (Sets.isNotEmpty(allPermissionItemIds)) {
            permissionAssignRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(
                    containerId, userGroupId, allPermissionItemIds);
            List<Permission> itemPermissions = permissionCache.findAllByItemIdIn(allPermissionItemIds);
            assignPermissions = new ArrayList<>(itemPermissions);
            log.info("通过配置项所有权限分配权限点 {}条", assignPermissions.size());

            List<Long> removePermissionIds = new ArrayList<>();
            for (Permission permission : permissions) {
                long itemId = permission.getItemId();
                if (!allPermissionItemIds.contains(itemId)) {
                    assignPermissions.add(permission);
                    removePermissionIds.add(permission.getId());
                }
            }
            log.info("直接分配权限 {}条", removePermissionIds.size());
            permissionAssignRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(
                    containerId, userGroupId, removePermissionIds);
        }
        List<PermissionAssign> assigns = entityFactory.assignPermissions(
                containerId, userGroupId, assign, inheritable, mfa, assignPermissions);
        permissionAssignRepository.insert(assigns);
    }
}
