package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.lang.*;
import cn.idealio.framework.spring.matcher.PathMatchers;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.tbac.application.TbacHandler;
import cn.sh.ideal.iam.permission.tbac.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 不适用缓存加速
 *
 * @author 宋志宗 on 2024/5/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CachelessTbacHandler implements TbacHandler {
    protected final UserRepository userRepository;
    protected final PermissionCache permissionCache;
    protected final SecurityContainerCache securityContainerCache;
    protected final PermissionAssignRepository permissionAssignRepository;

    /**
     * 获取用户在指定安全容器上所有可见的权限ID
     * <p>
     * 这个方法主要用于前端渲染用户有权限的页面元素
     *
     * @param userId      用户ID
     * @param containerId 容器ID
     * @return 可见权限ID列表
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    @Override
    public Set<Long> visiblePermissionIds(long userId, long containerId, long appId) {
        // 获取用户在指定节点上的所有权限, 包括他的所有子节点
        Collection<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, true, appId);
        // 将权限转换为权限ID的集合
        return assignedPermissions.stream()
                .map(assignedPermission -> assignedPermission.getPermission().getId())
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<Long> authorityContainerIds(long userId,
                                           @Nonnull String authority,
                                           @Nullable Long baseContainerId) {
        // 获取用户在各个容器节点上的权限配置信息, [authority]有权限配置的containerId -> 是否分配 -> 是否继承
        Map<Long, Tuple<Boolean, Boolean>> containerAssignMap =
                authorityContainerAssignInfo(userId, authority);
        // 通过权限配置信息分析出所有有权限的容器ID
        return analyzeContainerIds(baseContainerId, containerAssignMap);
    }

    /**
     * 通过权限配置信息分析出所有有权限的容器ID
     *
     * @param containerAssignMap containerId -> 是否分配 -> 是否继承
     * @return 容器ID列表
     */
    @Nonnull
    public Set<Long> analyzeContainerIds(
            @Nonnull Map<Long, Tuple<Boolean, Boolean>> containerAssignMap) {
        return analyzeContainerIds(null, containerAssignMap);
    }

    /**
     * 通过权限配置信息分析出所有有权限的容器ID
     *
     * @param baseContainerId    基础容器ID, 只分析这个节点及其下所有节点的容器
     * @param containerAssignMap containerId -> 是否分配 -> 是否继承
     * @return 容器ID列表
     */
    @Nonnull
    public Set<Long> analyzeContainerIds(
            @Nullable Long baseContainerId,
            @Nonnull Map<Long, Tuple<Boolean, Boolean>> containerAssignMap) {
        if (containerAssignMap.isEmpty()) {
            return Set.of();
        }

        Set<Long> containerIds = containerAssignMap.keySet();
        List<AnalyzedSecurityContainer> analyzedContainers =
                securityContainerCache.findAllById(containerIds);
        // 筛选出所有的顶层容器
        List<AnalyzedSecurityContainer> rootContainers =
                AnalyzedSecurityContainer.filterRoots(analyzedContainers);
        if (baseContainerId != null) {
            rootContainers = AnalyzedSecurityContainer.cut(baseContainerId, rootContainers);
        }
        if (rootContainers.isEmpty()) {
            return Set.of();
        }

        // 安全容器ID -> 是否分配 -> 是否继承
        Map<Long, Tuple<Boolean, Boolean>> analyzedMap = new HashMap<>();
        analyzeAuthorityContainers(rootContainers, containerAssignMap, analyzedMap);
        Set<Long> result = new HashSet<>();
        analyzedMap.forEach((containerId, tuple) -> {
            boolean assigned = tuple.getFirst();
            if (assigned) {
                result.add(containerId);
            }
        });
        return result;
    }

    /**
     * 分析权限容器，根据父容器的权限分配情况来确定子容器的权限状态。
     *
     * @param analyzedContainers 需要分析的权限容器列表，每个容器包含了权限分析结果。
     * @param containerAssignMap 容器分配映射，记录了容器ID与权限分配（是否分配，是否可继承）的对应关系。
     * @param analyzedMap        分析结果映射，记录了容器ID与权限分析结果（是否分配，是否可继承）的对应关系。
     */
    private void analyzeAuthorityContainers(
            @Nonnull List<AnalyzedSecurityContainer> analyzedContainers,
            @Nonnull Map<Long, Tuple<Boolean, Boolean>> containerAssignMap,
            @Nonnull Map<Long, Tuple<Boolean, Boolean>> analyzedMap) {
        // 遍历每个分析过的安全容器，以确定其权限状态
        for (AnalyzedSecurityContainer analyzedContainer : analyzedContainers) {
            SecurityContainer container = analyzedContainer.getContainer();
            Long containerId = container.getId();
            SequencedSet<Long> parentIds = container.parentIds();
            Tuple<Boolean, Boolean> containerTuple = containerAssignMap.get(containerId);
            // 如果当前容器直接在分配映射中存在，则将其状态直接复制到分析结果映射中
            if (containerTuple != null) {
                analyzedMap.put(containerId, containerTuple);
            } else if (Sets.isNotEmpty(parentIds)) {
                // 如果当前容器没有直接的权限分配信息，但是存在父容器，则尝试从父容器继承权限
                SequencedSet<Long> reversed = parentIds.reversed();
                for (Long parentId : reversed) {
                    Tuple<Boolean, Boolean> parentConfig = containerAssignMap.get(parentId);
                    // 如果父容器不存在分配信息，则跳过当前循环迭代
                    if (parentConfig == null) {
                        continue;
                    }
                    boolean assigned = parentConfig.getFirst();
                    boolean inheritable = parentConfig.getSecond();
                    // 如果父级分配了权限且是可继承的, 则该节点也有此权限并且是可继承的
                    // 否则, 权限到这一节点就中断掉了
                    if (assigned && inheritable) {
                        analyzedMap.put(containerId, Tuple.of(true, true));
                    } else {
                        analyzedMap.put(containerId, Tuple.of(false, false));
                    }
                    // 在确定了当前容器的权限状态后，跳出循环，不再向上继续继承权限
                    break;
                }
            }
            // 如果当前容器存在子容器，则递归分析子容器的权限状态
            List<AnalyzedSecurityContainer> childTree = analyzedContainer.getChildTree();
            if (Lists.isNotEmpty(childTree)) {
                analyzeAuthorityContainers(childTree, containerAssignMap, analyzedMap);
            }
        }
    }

    /**
     * 指定权限标识, 获取这个权限标识在各个容器节点上的权限配置信息
     *
     * @param userId    用户ID
     * @param authority 权限标识
     * @return [authority]有权限配置的containerId -> 是否分配 -> 是否继承
     */
    @Nonnull
    @Override
    public Map<Long, Tuple<Boolean, Boolean>>
    authorityContainerAssignInfo(long userId, @Nonnull String authority) {
        return containerAssignInfo(userId,
                permission -> permission.getAuthorities().contains(authority)
        );
    }

    @Nonnull
    @Override
    public Map<Long, Tuple<Boolean, Boolean>>
    permissionContainerAssignInfo(long userId, long permissionId) {
        return containerAssignInfo(userId, permission -> permission.getId() == permissionId);
    }

    @Nonnull
    private Map<Long, Tuple<Boolean, Boolean>> containerAssignInfo(
            long userId, @Nonnull Predicate<Permission> predicate) {
        Map<Long, Collection<PermissionAssignDetail>>
                assignDetails = getPermissionAssignDetails(userId);
        if (assignDetails.isEmpty()) {
            return Map.of();
        }
        // 直接分配了权限的containerId -> 是否分配 -> 是否继承
        Map<Long, Tuple<Boolean, Boolean>> containerAssignMap = new HashMap<>();
        assignDetails.forEach((containerId, details) -> {
            for (PermissionAssignDetail detail : details) {
                Permission permission = detail.getPermission();
                if (!predicate.test(permission)) {
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

    @Nonnull
    @Override
    public Set<Long> containerPermissionIds(long userId, long containerId,
                                            @Nonnull Set<Long> permissionIds) {

        Set<Long> containerIds = Set.of(containerId);
        Map<Long, Set<Long>> map = containerPermissionIds(userId, containerIds, permissionIds);
        return map.getOrDefault(containerId, Set.of());
    }

    @Nonnull
    @Override
    public Map<Long, Set<Long>> containerPermissionIds(long userId,
                                                       @Nonnull Set<Long> containerIds,
                                                       @Nonnull Set<Long> permissionIds) {
        // containerId -> 权限分配信息列表
        Map<Long, Collection<PermissionAssignDetail>> assignMap = getPermissionAssignDetails(userId);
        if (assignMap.isEmpty()) {
            log.info("批量过滤容器权限ID列表: 用户[{}]在任何安全容器节点上都没有分配权限", userId);
            HashMap<Long, Set<Long>> result = new HashMap<>();
            for (Long containerId : containerIds) {
                result.put(containerId, Set.of());
            }
            return result;
        }
        Map<Long, Set<Long>> result = new HashMap<>();
        for (long containerId : containerIds) {
            AnalyzedSecurityContainer container = securityContainerCache.findById(containerId);
            if (container == null) {
                log.warn("批量过滤容器权限ID列表:  安全容器[{}]不存在", containerId);
                result.put(containerId, Set.of());
                continue;
            }
            Set<Long> hasPermissionIds = new HashSet<>();
            SequencedSet<Long> containerParentIds = container.getParentIds();
            LinkedHashSet<Long> containerIdChain = new LinkedHashSet<>(containerParentIds);
            containerIdChain.add(containerId);
            for (long loopContainerId : containerIdChain) {
                Collection<PermissionAssignDetail> details = assignMap.get(loopContainerId);
                if (CollectionUtils.isEmpty(details)) {
                    continue;
                }
                for (PermissionAssignDetail detail : details) {
                    Permission permission = detail.getPermission();
                    long permissionId = permission.getId();
                    if (!permissionIds.contains(permissionId)) {
                        continue;
                    }
                    // 如果是不授权, 则移除
                    if (!detail.isAssigned()) {
                        hasPermissionIds.remove(permissionId);
                        continue;
                    }
                    // 父节点且不继承, 那么权限到这一步也就中断了
                    boolean inheritable = detail.isInheritable();
                    if (!inheritable && loopContainerId != containerId) {
                        hasPermissionIds.remove(permissionId);
                        continue;
                    }
                    // 最后留下来的就是有权限的
                    hasPermissionIds.add(permissionId);
                }
            }
            result.put(containerId, hasPermissionIds);
        }
        return result;
    }

    @Override
    public boolean hasAuthority(long userId, long containerId, @Nonnull String authority) {
        return checkAuthority(userId, containerId, permission ->
                permission.getAuthorities().contains(authority)
        );
    }

    @Override
    public boolean hasAnyAuthority(long userId, long containerId,
                                   @Nonnull Set<String> authorities) {
        return checkAuthority(userId, containerId, permission ->
                permission.getAuthorities().stream().anyMatch(authorities::contains)
        );
    }

    private boolean checkAuthority(long userId, long containerId, Predicate<Permission> predicate) {
        return getAssignedPermissions(userId, containerId, true).stream()
                .map(AssignedPermission::getPermission)
                .anyMatch(predicate);
    }


    @Override
    public boolean hasApiPermission(long userId, long containerId,
                                    @Nonnull String method, @Nonnull String path) {
        Collection<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, true);
        if (assignedPermissions.isEmpty()) {
            return false;
        }
        String fullPath = method + " " + path;
        log.debug("Api权限校验fullPath = {}", fullPath);
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            Set<String> specificApis = permission.getSpecificApis();
            if (specificApis.contains(path) || specificApis.contains(fullPath)) {
                return true;
            }
            Set<String> patterns = permission.getApiPatterns();
            for (String pattern : patterns) {
                String[] split = StringUtils.split(pattern, " ");
                if (split.length == 1) {
                    if (PathMatchers.match(split[0], path)) {
                        return true;
                    }
                }
                if (!method.equalsIgnoreCase(split[0])) {
                    continue;
                }
                if (PathMatchers.match(split[1], path)) {
                    return true;
                }
            }
        }
        log.info("用户[{}]在安全容器[{}]上没有此api权限: [{} {}]", userId, containerId, method, path);
        return false;
    }

    @Override
    public boolean needMfa(long userId, long containerId, long permissionId) {
        List<PermissionAssign> assigns = getPermissionAssigns(userId, permissionId);
        if (assigns.isEmpty()) {
            log.info("mfa验证失败, 用户[{}]在安全容器[{}]上没有分配权限[{}]",
                    userId, containerId, permissionId);
            return false;
        }
        Map<Long, Collection<PermissionAssignDetail>>
                assignDetails = getPermissionAssignDetails(assigns);
        Collection<AssignedPermission> assignedPermissions =
                getAssignedPermissions(containerId, false, null, assignDetails);
        for (AssignedPermission permission : assignedPermissions) {
            long assignedPermissionId = permission.getPermission().getId();
            if (assignedPermissionId == permissionId) {
                return permission.isMfa();
            }
        }
        log.error("mfa验证失败, 用户[{}]在安全容器[{}]上没有分配权限[{}]",
                userId, containerId, permissionId);
        return false;
    }

    @Nonnull
    @Override
    public PermissionAssignable assignable(long userId, long containerId, long appId) {
        Collection<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, false, appId);
        if (assignedPermissions.isEmpty()) {
            return PermissionAssignable.EMPTY;
        }
        Set<Long> groupIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            if (permission.isGroupSecurity()) {
                groupIds.add(permission.getGroupId());
            }
            if (permission.isItemSecurity()) {
                itemIds.add(permission.getItemId());
            }
        }

        PermissionAssignable assignable = new PermissionAssignable();
        assignable.setGroupIds(groupIds);
        assignable.setItemIds(itemIds);
        return assignable;
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
    public Collection<AssignedPermission> getAssignedPermissions(long userId,
                                                                 long containerId,
                                                                 boolean includeChildren) {
        return getAssignedPermissions(userId, containerId, includeChildren, null);
    }

    /**
     * 获取用户指定节点上的拥有的所有权限
     *
     * @param userId          用户ID
     * @param containerId     节点ID
     * @param includeChildren 是否包含指定节点的所有层级子节点, 否则只获取在指定节点之上的权限
     * @param appId           应用ID, 如果不为空则只获取该应用下的权限
     * @return 用户在该节点上拥有的所有权限
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    public Collection<AssignedPermission> getAssignedPermissions(long userId,
                                                                 long containerId,
                                                                 boolean includeChildren,
                                                                 @Nullable Long appId) {
        // containerId -> 权限分配信息列表
        Map<Long, Collection<PermissionAssignDetail>> assignMap = getPermissionAssignDetails(userId);
        if (assignMap.isEmpty()) {
            log.info("用户[{}]在任何安全容器节点上都没有分配权限", userId);
            return List.of();
        }
        return getAssignedPermissions(containerId, includeChildren, appId, assignMap);
    }

    /**
     * 获取用户指定节点上的拥有的所有权限
     *
     * @param containerId     节点ID
     * @param includeChildren 是否包含指定节点的所有层级子节点, 否则只获取在指定节点之上的权限
     * @param appId           应用ID, 如果不为空则只获取该应用下的权限
     * @param assignMap       容器ID -> 权限配置信息
     * @return 用户在该节点上拥有的所有权限
     * @author 宋志宗 on 2024/5/18
     */
    @Nonnull
    public Collection<AssignedPermission> getAssignedPermissions(
            long containerId, boolean includeChildren, @Nullable Long appId,
            @Nonnull Map<Long, Collection<PermissionAssignDetail>> assignMap) {
        AnalyzedSecurityContainer container = securityContainerCache.findById(containerId);
        if (container == null) {
            log.warn("获取用户指定节点上的拥有的所有权限失败, 安全容器[{}]不存在", containerId);
            return List.of();
        }
        // 权限ID -> 权限分配信息
        Map<Long, AssignedPermission> assignedPermissionMap = new HashMap<>();
        SequencedSet<Long> containerParentIds = container.getParentIds();
        LinkedHashSet<Long> containerIds = new LinkedHashSet<>(containerParentIds);
        containerIds.add(containerId);
        for (long loopContainerId : containerIds) {
            Collection<PermissionAssignDetail> details = assignMap.get(loopContainerId);
            if (CollectionUtils.isEmpty(details)) {
                continue;
            }
            for (PermissionAssignDetail detail : details) {
                Permission permission = detail.getPermission();
                if (appId != null && appId != permission.getAppId()) {
                    continue;
                }
                long permissionId = permission.getId();
                // 如果是不授权, 则移除
                if (!detail.isAssigned()) {
                    assignedPermissionMap.remove(permissionId);
                    continue;
                }
                // 父节点且不继承, 则直接跳过, 不需要加入到权限列表
                boolean inheritable = detail.isInheritable();
                if (!inheritable && loopContainerId != containerId) {
                    // 中间变成了不继承, 那么权限到这一步也就中断了
                    assignedPermissionMap.remove(permissionId);
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
                Collection<PermissionAssignDetail> details = assignMap.get(analyzedContainerId);
                if (CollectionUtils.isEmpty(details)) {
                    continue;
                }
                for (PermissionAssignDetail detail : details) {
                    if (!detail.isAssigned()) {
                        continue;
                    }
                    Permission permission = detail.getPermission();
                    long permissionId = permission.getId();
                    if (assignedPermissionMap.containsKey(permissionId)) {
                        continue;
                    }
                    assignedPermissionMap.put(permissionId, new AssignedPermission(detail));
                }
            }
        }
        return assignedPermissionMap.values();
    }

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
            log.info("获取所有权限分配信息返回空, 用户[{}]未关联任何用户组", userId);
            return List.of();
        }
        return permissionAssignRepository.findAllByUserGroupIdIn(userGroupIds);
    }

    @Nonnull
    public List<PermissionAssign> getPermissionAssigns(long userId, long permissionId) {
        List<Long> userGroupIds = userRepository.getGroupIds(userId);
        if (userGroupIds.isEmpty()) {
            log.info("获取指定权限分配信息返回空, 用户[{}]未关联任何用户组", userId);
            return List.of();
        }
        return permissionAssignRepository
                .findAllByPermissionIdAndUserGroupIdIn(permissionId, userGroupIds);
    }

    /**
     * 获取用户在各个容器节点上的权限配置信息
     * <p>
     * 这个方法的返回值可以用于分析用户在各个容器节点上的权限配置。
     * 例如：哪些权限已经被分配，哪些权限是可继承的，以及哪些权限需要多因素认证。
     *
     * @param userId 用户ID
     * @return 容器ID -> 权限配置信息
     */
    @Nonnull
    public Map<Long, Collection<PermissionAssignDetail>> getPermissionAssignDetails(long userId) {
        List<PermissionAssign> assigns = getAllAssigns(userId);
        if (assigns.isEmpty()) {
            log.info("用户[{}]未配置任何权限", userId);
            return Map.of();
        }
        return getPermissionAssignDetails(assigns);
    }

    /**
     * 获取用户在各个容器节点上的权限配置信息
     * <p>
     * 这个方法的返回值可以用于分析用户在各个容器节点上的权限配置。
     * 例如：哪些权限已经被分配，哪些权限是可继承的，以及哪些权限需要多因素认证。
     *
     * @param assigns 权限分配信息
     * @return 容器ID -> 权限配置信息
     */
    @Nonnull
    public Map<Long, Collection<PermissionAssignDetail>>
    getPermissionAssignDetails(@Nonnull List<PermissionAssign> assigns) {
        // 按安全容器ID分组 containerId -> permissionId -> 权限分配信息
        Map<Long, Map<Long, PermissionAssignDetail>>
                containerPermissionAssignMap = new HashMap<>();
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
            Map<Long, PermissionAssignDetail> permissionMap = containerPermissionAssignMap
                    .computeIfAbsent(containerId, k -> new HashMap<>());
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
        Map<Long, Collection<PermissionAssignDetail>> containerAssignMap = new HashMap<>();
        containerPermissionAssignMap.forEach((containerId, assignMap) -> {
            Collection<PermissionAssignDetail> details = assignMap.values();
            containerAssignMap.put(containerId, details);
        });
        return containerAssignMap;
    }
}
