package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.lang.Lists;
import cn.idealio.framework.lang.Tuple;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.tbac.application.TbacHandler;
import cn.sh.ideal.iam.permission.tbac.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author 宋志宗 on 2024/5/18
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractTbacHandler implements TbacHandler {
    protected final UserRepository userRepository;
    protected final PermissionCache permissionCache;
    protected final SecurityContainerCache securityContainerCache;
    protected final PermissionAssignRepository permissionAssignRepository;

    /**
     * 获取用户通过用户组关联的所有权限分配信息
     *
     * @param userId 用户ID
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    public List<PermissionAssign> getAllAssigns(long userId) {
        List<Long> userGroupIds = userRepository.getGroupIds(userId);
        if (userGroupIds.isEmpty()) {
            log.info("用户[{}]未关联任何用户组", userId);
            return List.of();
        }
        return permissionAssignRepository.findAllByUserGroupIdIn(userGroupIds);
    }

    /**
     * 获取用户在各个容器节点上的权限配置信息
     *
     * @param userId 用户ID
     * @return 容器ID -> 权限配置信息
     */
    @Nonnull
    public Map<Long, List<PermissionAssignDetail>> getPermissionAssignDetails(long userId) {
        List<PermissionAssign> assigns = getAllAssigns(userId);
        if (assigns.isEmpty()) {
            log.info("用户[{}]未配置任何权限", userId);
            return Map.of();
        }
        // 按安全容器ID分组
        Map<Long, Map<Long, PermissionAssignDetail>> containerAssigndPermissionMap = new HashMap<>();
        for (PermissionAssign permissionAssign : assigns) {
            long containerId = permissionAssign.getContainerId();
            long permissionId = permissionAssign.getPermissionId();
            Permission permission = permissionCache.findById(permissionId);
            if (permission == null || !permission.available()) {
                log.info("用户权限树分析: 权限[{}]不存在或者不可用", permissionId);
                continue;
            }
            boolean mfa = permissionAssign.isMfa();
            boolean assigned = permissionAssign.isAssigned();
            boolean inheritable = permissionAssign.isInheritable();
            Map<Long, PermissionAssignDetail> permissionMap =
                    containerAssigndPermissionMap.computeIfAbsent(containerId, k -> new HashMap<>());
            PermissionAssignDetail detail = permissionMap.get(permissionId);
            if (detail == null) {
                PermissionAssignDetail assignDetail =
                        new PermissionAssignDetail(mfa, assigned, inheritable, permission);
                permissionMap.put(permissionId, assignDetail);
            } else {
                detail.setMfa(mfa);
                detail.setAssigned(assigned);
                detail.setInheritable(inheritable);
            }
        }
        Map<Long, List<PermissionAssignDetail>> containerAssignMap = new HashMap<>();
        containerAssigndPermissionMap.forEach((containerId, assignMap) -> {
            Collection<PermissionAssignDetail> details = assignMap.values();
            List<PermissionAssignDetail> detailList = new ArrayList<>(details);
            containerAssignMap.put(containerId, detailList);
        });
        return containerAssignMap;
    }

    @Nonnull
    @Override
    public Map<Long, Tuple<Boolean, Boolean>> authorityContainerAssignMap(long userId, @Nonnull String authority) {
        Map<Long, List<PermissionAssignDetail>> assignDetails = getPermissionAssignDetails(userId);
        if (assignDetails.isEmpty()) {
            return Map.of();
        }
        // 直接分配了权限的containerId -> 是否分配 -> 是否继承
        Map<Long, Tuple<Boolean, Boolean>> containerAssignMap = new HashMap<>();
        assignDetails.forEach((containerId, details) -> {
            for (PermissionAssignDetail detail : details) {
                Set<String> authorities = detail.getPermission().getAuthorities();
                if (!authorities.contains(authority)) {
                    continue;
                }
                boolean assigned = detail.isAssigned();
                boolean inheritable = detail.isInheritable();

                Tuple<Boolean, Boolean> tuple = containerAssignMap.get(containerId);
                if (tuple == null) {
                    containerAssignMap.put(containerId, Tuple.of(assigned, inheritable));
                } else {
                    assigned = tuple.getFirst() || assigned;
                    inheritable = tuple.getSecond() || inheritable;
                    tuple.setFirst(assigned);
                    tuple.setSecond(inheritable);
                }
                // 如果分配和继承都为true, 则跳过循环.
                // 因为分配和继承相对禁用和不继承优先级更高, 这两者都是true就没必要继续算下去了
                if (assigned && inheritable) {
                    break;
                }
            }
        });
        return containerAssignMap;
    }

    /**
     * 获取用户指定节点上的拥有的所有权限
     *
     * @param userId          用户ID
     * @param containerId     节点ID
     * @param includeChildren 是否包含指定节点的所有层级子节点, 否则只获取在指定节点之上的权限
     * @return 用户在该节点上拥有的所有权限
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    public List<AssignedPermission> getAssignedPermissions(long userId,
                                                           long containerId,
                                                           boolean includeChildren) {
        AnalyzedSecurityContainer container = securityContainerCache.findById(containerId);
        if (container == null) {
            log.warn("获取用户指定节点上的拥有的所有权限失败, 安全容器[{}]不存在", containerId);
            return List.of();
        }
        // containerId -> 权限分配信息列表
        Map<Long, List<PermissionAssignDetail>> assignMap = getPermissionAssignDetails(userId);
        if (assignMap.isEmpty()) {
            log.info("用户[{}]在任何安全容器节点上都没有分配权限", userId);
            return List.of();
        }
        // 权限ID -> 权限分配信息
        Map<Long, AssignedPermission> assignedPermissionMap = new HashMap<>();
        SequencedSet<Long> containerParentIds = container.getParentIds();
        LinkedHashSet<Long> containerIds = new LinkedHashSet<>(containerParentIds);
        containerIds.add(containerId);
        for (Long loopContainerId : containerIds) {
            List<PermissionAssignDetail> details = assignMap.get(loopContainerId);
            if (Lists.isEmpty(details)) {
                continue;
            }
            for (PermissionAssignDetail detail : details) {
                Permission permission = detail.getPermission();
                Long permissionId = permission.getId();
                // 如果是不授权, 则移除
                if (!detail.isAssigned()) {
                    assignedPermissionMap.remove(permissionId);
                }
                // 父节点且不继承, 则直接跳过, 不需要加入到权限列表
                boolean inheritable = detail.isInheritable();
                if (!inheritable && loopContainerId != containerId) {
                    continue;
                }
                AssignedPermission exists = assignedPermissionMap.get(permissionId);
                if (exists == null) {
                    assignedPermissionMap.put(permissionId, new AssignedPermission(detail));
                } else {
                    exists.updateMfa(detail.isMfa());
                }
            }
        }
        if (includeChildren) {
            Set<Long> assignedContainerIds = assignMap.keySet();
            List<AnalyzedSecurityContainer> containers =
                    securityContainerCache.findAllById(assignedContainerIds);
            for (AnalyzedSecurityContainer analyzedSecurityContainer : containers) {
                SequencedSet<Long> parentIds = analyzedSecurityContainer.getParentIds();
                if (!parentIds.contains(containerId)) {
                    continue;
                }
                long analyzedContainerId = analyzedSecurityContainer.getContainer().getId();
                List<PermissionAssignDetail> details = assignMap.get(analyzedContainerId);
                if (Lists.isEmpty(details)) {
                    continue;
                }
                for (PermissionAssignDetail detail : details) {
                    if (!detail.isAssigned()) {
                        continue;
                    }
                    Permission permission = detail.getPermission();
                    Long permissionId = permission.getId();
                    if (assignedPermissionMap.containsKey(permissionId)) {
                        continue;
                    }
                    assignedPermissionMap.put(permissionId, new AssignedPermission(detail));
                }
            }
        }
        Collection<AssignedPermission> values = assignedPermissionMap.values();
        if (values.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(values);
    }
}
