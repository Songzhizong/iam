package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.lang.Lists;
import cn.idealio.framework.lang.Sets;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.lang.Tuple;
import cn.idealio.framework.spring.matcher.PathMatchers;
import cn.sh.ideal.iam.organization.domain.model.AnalyzedSecurityContainer;
import cn.sh.ideal.iam.organization.domain.model.SecurityContainer;
import cn.sh.ideal.iam.organization.domain.model.SecurityContainerCache;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.tbac.domain.model.AssignedPermission;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignDetail;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignRepository;
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

    public CachelessTbacHandler(UserRepository userRepository,
                                PermissionCache permissionCache,
                                SecurityContainerCache securityContainerCache,
                                PermissionAssignRepository permissionAssignRepository) {
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
        Map<Long, List<PermissionAssignDetail>> assignDetails = getPermissionAssignDetails(userId);
        Set<Long> containerIds = assignDetails.keySet();
        List<AnalyzedSecurityContainer> analyzedContainers = securityContainerCache.findAllById(containerIds);
        // 筛选出所有的顶层容器
        List<AnalyzedSecurityContainer> filteredContainers = new ArrayList<>(analyzedContainers);
        filteredContainers.removeIf(analyzedContainer -> {
            SecurityContainer container = analyzedContainer.getContainer();
            long containerId = container.getId();
            if (containerIds.contains(containerId)) {
                return false;
            }
            SequencedSet<Long> parentIds = analyzedContainer.getParentIds();
            if (parentIds.isEmpty()) {
                return true;
            }
            return !Sets.containsAny(containerIds, parentIds);
        });
        if (baseContainerId != null) {
            filteredContainers = cutContainers(baseContainerId, filteredContainers);
        }
        if (filteredContainers.isEmpty()) {
            return Set.of();
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
        Map<Long, Tuple<Boolean, Boolean>> analyzedMap = new HashMap<>();
        analyzeAuthorityContainers(filteredContainers, containerAssignMap, analyzedMap);
        Set<Long> result = new HashSet<>();
        analyzedMap.forEach((containerId, tuple) -> {
            boolean assigned = tuple.getFirst();
            if (assigned) {
                result.add(containerId);
            }
        });
        return result;
    }

    private void analyzeAuthorityContainers(@Nonnull List<AnalyzedSecurityContainer> analyzedContainers,
                                            @Nonnull Map<Long, Tuple<Boolean, Boolean>> containerAssignMap,
                                            @Nonnull Map<Long, Tuple<Boolean, Boolean>> analyzedMap) {
        for (AnalyzedSecurityContainer analyzedContainer : analyzedContainers) {
            SecurityContainer container = analyzedContainer.getContainer();
            Long containerId = container.getId();
            SequencedSet<Long> parentIds = container.parentIds();
            Tuple<Boolean, Boolean> containerTuple = containerAssignMap.get(containerId);
            if (containerTuple != null) {
                analyzedMap.put(containerId, containerTuple);
            } else if (Sets.isNotEmpty(parentIds)) {
                SequencedSet<Long> reversed = parentIds.reversed();
                for (Long parentId : reversed) {
                    Tuple<Boolean, Boolean> parentConfig = containerAssignMap.get(parentId);
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
                    break;
                }
            }
            // 如果有子级树, 则递归执行权限关联安全容器分析
            List<AnalyzedSecurityContainer> childTree = analyzedContainer.getChildTree();
            if (Lists.isNotEmpty(childTree)) {
                analyzeAuthorityContainers(childTree, containerAssignMap, analyzedMap);
            }
        }
    }

    @Nonnull
    private List<AnalyzedSecurityContainer> cutContainers(long baseContainerId,
                                                          @Nonnull List<AnalyzedSecurityContainer> containers) {
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
