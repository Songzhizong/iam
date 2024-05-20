package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.lang.Lists;
import cn.idealio.framework.lang.Sets;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.lang.Tuple;
import cn.idealio.framework.spring.matcher.PathMatchers;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.tbac.domain.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 不适用缓存加速
 *
 * @author 宋志宗 on 2024/5/18
 */
@Slf4j
@Component
public class CachelessTbacHandler extends AbstractTbacHandler {

    public CachelessTbacHandler(@Nonnull UserRepository userRepository,
                                @Nonnull PermissionCache permissionCache,
                                @Nonnull SecurityContainerCache securityContainerCache,
                                @Nonnull PermissionAssignRepository permissionAssignRepository) {
        super(userRepository, permissionCache, securityContainerCache, permissionAssignRepository);
    }

    @Nonnull
    @Override
    public Set<Long> visiblePermissionIds(long userId, long containerId) {
        List<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, true);
        if (assignedPermissions.isEmpty()) {
            return Set.of();
        }
        Set<Long> permissionIds = new HashSet<>();
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            Long permissionId = permission.getId();
            permissionIds.add(permissionId);
        }
        return permissionIds;
    }

    @Nonnull
    @Override
    public Set<Long> authorityContainerIds(long userId,
                                           @Nonnull String authority,
                                           @Nullable Long baseContainerId) {
        // [authority]有权限配置的containerId -> 是否分配 -> 是否继承
        Map<Long, Tuple<Boolean, Boolean>> containerAssignMap =
                authorityContainerAssignMap(userId, authority);
        return authorityContainerIds(baseContainerId, containerAssignMap);
    }

    /**
     * 通过权限配置信息分析出所有有权限的容器ID
     *
     * @param containerAssignMap containerId -> 是否分配 -> 是否继承
     * @return 容器ID列表
     */
    @Nonnull
    public Set<Long> authorityContainerIds(@Nonnull Map<Long, Tuple<Boolean, Boolean>> containerAssignMap) {
        return authorityContainerIds(null, containerAssignMap);
    }

    /**
     * 通过权限配置信息分析出所有有权限的容器ID
     *
     * @param baseContainerId    基础容器ID, 只分析这个节点及其下所有节点的容器
     * @param containerAssignMap containerId -> 是否分配 -> 是否继承
     * @return 容器ID列表
     */
    @Nonnull
    public Set<Long> authorityContainerIds(@Nullable Long baseContainerId,
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
            rootContainers = cutContainers(baseContainerId, rootContainers);
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
    private void analyzeAuthorityContainers(@Nonnull List<AnalyzedSecurityContainer> analyzedContainers,
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

    @Nonnull
    private List<AnalyzedSecurityContainer>
    cutContainers(long baseContainerId, @Nonnull List<AnalyzedSecurityContainer> containers) {
        if (containers.isEmpty()) {
            return List.of();
        }
        for (AnalyzedSecurityContainer analyzedSecurityContainer : containers) {
            long containerId = analyzedSecurityContainer.getContainer().getId();
            if (containerId == baseContainerId) {
                return List.of(analyzedSecurityContainer);
            }
            List<AnalyzedSecurityContainer> childTree = analyzedSecurityContainer.getChildTree();
            if (Lists.isEmpty(childTree)) {
                return List.of();
            }
            List<AnalyzedSecurityContainer> list = cutContainers(baseContainerId, childTree);
            if (Lists.isNotEmpty(list)) {
                return list;
            }
        }
        return List.of();
    }

    @Nonnull
    @Override
    public Set<Long> containerPermissionIds(long userId, long containerId,
                                            @Nonnull Set<Long> permissionIds) {
        List<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, false);
        if (assignedPermissions.isEmpty()) {
            return Set.of();
        }
        Set<Long> filteredPermissionIds = new HashSet<>();
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            Long permissionId = permission.getId();
            if (permissionIds.contains(permissionId)) {
                filteredPermissionIds.add(permissionId);
            }
        }
        return filteredPermissionIds;
    }

    @Nonnull
    @Override
    public Map<Long, Set<Long>> containerPermissionIds(long userId,
                                                       @Nonnull Set<Long> containerIds,
                                                       @Nonnull Set<Long> permissionIds) {
        Map<Long, Set<Long>> result = new HashMap<>();
        for (Long containerId : containerIds) {
            Set<Long> filtered = containerPermissionIds(userId, containerId, permissionIds);
            result.put(containerId, filtered);
        }
        return result;
    }

    @Override
    public boolean hasAuthority(long userId, long containerId, @Nonnull String authority) {
        List<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, true);
        if (assignedPermissions.isEmpty()) {
            return false;
        }
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            Set<String> authorities = permission.getAuthorities();
            if (authorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAnyAuthority(long userId, long containerId,
                                   @Nonnull Collection<String> authorities) {
        List<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, true);
        if (assignedPermissions.isEmpty()) {
            return false;
        }
        if (!(authorities instanceof Set<String>)) {
            authorities = new HashSet<>(authorities);
        }
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            Set<String> permissionAuthorities = permission.getAuthorities();
            if (Sets.containsAny(permissionAuthorities, authorities)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAllAuthority(long userId, long containerId,
                                   @Nonnull Collection<String> authorities) {
        if (authorities.size() == 1) {
            for (String authority : authorities) {
                return hasAuthority(userId, containerId, authority);
            }
            return false;
        }
        List<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, true);
        if (assignedPermissions.isEmpty()) {
            return false;
        }
        if (!(authorities instanceof Set<String>)) {
            authorities = new HashSet<>(authorities);
        }
        int authoritiesSize = authorities.size();
        Set<String> existsAuthorities = new HashSet<>();
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            Set<String> permissionAuthorities = permission.getAuthorities();
            for (String authority : authorities) {
                if (permissionAuthorities.contains(authority)) {
                    existsAuthorities.add(authority);
                    if (existsAuthorities.size() == authoritiesSize) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasApiPermission(long userId, long containerId,
                                    @Nonnull String method, @Nonnull String path) {
        List<AssignedPermission> assignedPermissions =
                getAssignedPermissions(userId, containerId, true);
        if (assignedPermissions.isEmpty()) {
            return false;
        }
        String fullPath = method + " " + path;
        log.debug("Api权限校验fullPath = {}", fullPath);
        Set<String> patterns = new HashSet<>();
        for (AssignedPermission assignedPermission : assignedPermissions) {
            Permission permission = assignedPermission.getPermission();
            Set<String> specificApis = permission.getSpecificApis();
            if (specificApis.contains(path) || specificApis.contains(fullPath)) {
                return true;
            }
            Set<String> apiPatterns = permission.getApiPatterns();
            patterns.addAll(apiPatterns);
        }
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
        log.info("用户[{}]在安全容器[{}]上没有此api权限: [{} {}]", userId, containerId, method, path);
        return false;
    }
}
