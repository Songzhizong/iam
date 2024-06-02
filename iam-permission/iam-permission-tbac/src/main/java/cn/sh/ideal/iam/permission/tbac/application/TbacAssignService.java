package cn.sh.ideal.iam.permission.tbac.application;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.lang.Sets;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.UserGroupRepository;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionRepository;
import cn.sh.ideal.iam.permission.tbac.domain.model.EntityFactory;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignRepository;
import cn.sh.ideal.iam.permission.tbac.domain.model.SecurityContainerRepository;
import cn.sh.ideal.iam.permission.tbac.dto.args.AssignPermissionArgs;
import cn.sh.ideal.iam.permission.tbac.dto.args.AssignPermissionsArgs;
import cn.sh.ideal.iam.permission.tbac.dto.args.CoverPermissionsArgs;
import cn.sh.ideal.iam.permission.tbac.dto.args.UnassignPermissionsArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TbacAssignService {
    private final IamI18nReader i18nReader;
    private final EntityFactory entityFactory;
    private final PermissionCache permissionCache;
    private final UserGroupRepository userGroupRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionAssignRepository permissionAssignRepository;
    private final SecurityContainerRepository securityContainerRepository;

    /**
     * 分配权限, 传入的权限点会覆盖, 未传入的权限点继续保留.
     * 这个方法用作新分配权限点, 不会删除已分配的权限点.
     *
     * @author 宋志宗 on 2024/5/22
     */
    @Transactional(rollbackFor = Throwable.class)
    public void assign(@Nonnull AssignPermissionsArgs args) {
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
            log.info("直接分配权限 {}条", assignPermissions.size());

            // 分别删除权限项和权限点的分配关系, 避免重复分配也能减少in查询的条件数量
            permissionAssignRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionItemIdIn(
                    containerId, userGroupId, allPermissionItemIds);
            permissionAssignRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(
                    containerId, userGroupId, removePermissionIds);
        } else {
            // 删除接下来需要分配的权限点的分配关系, 避免重复分配导致唯一索引冲突
            Set<Long> removePermissionIds = assignPermissions.stream()
                    .map(Permission::getId).collect(Collectors.toSet());
            permissionAssignRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(
                    containerId, userGroupId, removePermissionIds);
        }
        List<PermissionAssign> assigns = entityFactory.assignPermissions(
                containerId, userGroupId, assign, inheritable, mfa, assignPermissions);
        permissionAssignRepository.insert(assigns);
    }

    /**
     * 取消分配权限点, 传入的权限点会从分配关系中删除
     *
     * @author 宋志宗 on 2024/5/22
     */
    @Transactional(rollbackFor = Throwable.class)
    public void unassign(@Nonnull UnassignPermissionsArgs args) {
        Long containerId = args.getContainerId();
        Long userGroupId = args.getUserGroupId();
        Set<Long> permissionIds = args.getPermissionIds();
        Asserts.nonnull(containerId, () -> i18nReader.getMessage("container_id.required"));
        Asserts.nonnull(userGroupId, () -> i18nReader.getMessage("user_group_id.required"));
        Asserts.notEmpty(permissionIds, () -> i18nReader.getMessage("permission_ids.required"));
        int count = permissionAssignRepository.deleteAllByContainerIdAndUserGroupIdAndPermissionIdIn(
                containerId, userGroupId, permissionIds);
        log.info("安全容器[{}]用户组[{}] 删除 {} 条权限分配记录", containerId, userGroupId, count);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void cover(@Nonnull CoverPermissionsArgs args) {
        Long containerId = args.getContainerId();
        Long userGroupId = args.getUserGroupId();
        Set<AssignPermissionArgs> permissionArgsSet = args.getPermissions();
        Asserts.nonnull(containerId, () -> i18nReader.getMessage("container_id.required"));
        Asserts.nonnull(userGroupId, () -> i18nReader.getMessage("user_group_id.required"));
        int deleteCount = permissionAssignRepository
                .deleteAllByContainerIdAndUserGroupId(containerId, userGroupId);
        log.info("覆盖权限分配: 安全容器[{}]用户组[{}] 删除 {} 条权限分配记录", containerId, userGroupId, deleteCount);
        if (Sets.isEmpty(permissionArgsSet)) {
            return;
        }
        Map<Long, AssignPermissionArgs> permissionArgsMap = new HashMap<>();
        for (AssignPermissionArgs permissionArgs : permissionArgsSet) {
            Long permissionId = permissionArgs.getPermissionId();
            Asserts.nonnull(permissionId, () -> i18nReader.getMessage("permission_id.required"));
            permissionArgsMap.put(permissionId, permissionArgs);
        }
        Set<Long> permissionIds = permissionArgsMap.keySet();
        List<Permission> permissions = permissionCache.findAllById(permissionIds);
        for (Permission permission : permissions) {
            long permissionId = permission.getId();
            boolean available = permission.available();
            if (!available) {
                permissionArgsMap.remove(permissionId);
                continue;
            }
            AssignPermissionArgs permissionArgs = permissionArgsMap.get(permissionId);
            Set<Long> childIds = permission.getChildIds();
            if (Sets.isNotEmpty(childIds)) {
                for (Long childPermissionId : childIds) {
                    Permission childPermission = permissionCache.findById(childPermissionId);
                    if (childPermission == null || !childPermission.available()) {
                        continue;
                    }
                    permissionArgsMap.put(childPermissionId, permissionArgs);
                }
            }
            if (permission.isAllInItem()) {
                long itemId = permission.getItemId();
                List<Permission> itemPermissions = permissionCache.findAllByItemId(itemId);
                for (Permission itemPermission : itemPermissions) {
                    if (itemPermission.available()) {
                        permissionArgsMap.put(itemPermission.getId(), permissionArgs);
                    }
                }
            }
        }
        List<PermissionAssign> assigns = new ArrayList<>();
        permissionArgsMap.forEach((permissionId, permissionArgs) -> {
            Permission permission = permissionCache.findById(permissionId);
            if (permission == null || !permission.available()) {
                return;
            }
            Boolean assign = Objects.requireNonNullElse(permissionArgs.getAssign(), true);
            Boolean mfa = Objects.requireNonNullElse(permissionArgs.getMfa(), false);
            Boolean inheritable = Objects.requireNonNullElse(permissionArgs.getInheritable(), false);
            PermissionAssign permissionAssign = entityFactory
                    .assignPermission(containerId, userGroupId, assign, inheritable, mfa, permission);
            assigns.add(permissionAssign);
        });
        permissionAssignRepository.insert(assigns);
    }

    /**
     * 为用户组在指定安全容器上分配所有权限
     *
     * @param appId       应用ID
     * @param containerId 安全容器ID
     * @param userGroupId 用户组ID
     * @param inheritable 分配的权限是否可被继承
     */
    @Transactional(rollbackFor = Throwable.class)
    public void assignAllPermission(long appId, long containerId,
                                    long userGroupId, boolean inheritable) {
        securityContainerRepository.findById(containerId).orElseThrow(() -> {
            log.info("分配所有权限失败, 安全容器不存在, id: {}", containerId);
            return new ResourceNotFoundException("安全容器不存在");
        });
        userGroupRepository.findById(userGroupId).orElseThrow(() -> {
            log.info("分配所有权限失败, 用户组不存在, id: {}", userGroupId);
            return new ResourceNotFoundException("用户组不存在");
        });
        permissionAssignRepository
                .deleteAllByAppIdAndContainerIdAndUserGroupId(appId, containerId, userGroupId);
        List<Permission> permissions = permissionRepository.findAllByAppId(appId);
        List<Permission> filter = permissions.stream().filter(Permission::available).toList();
        List<PermissionAssign> assigns = entityFactory.assignPermissions(
                containerId, userGroupId, true, inheritable, false, filter);
        permissionAssignRepository.insert(assigns);
    }
}
