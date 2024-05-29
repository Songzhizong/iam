package cn.sh.ideal.iam.permission.tbac.port.web;

import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.transmission.ListResult;
import cn.idealio.framework.transmission.Result;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.permission.tbac.application.SecurityContainerService;
import cn.sh.ideal.iam.permission.tbac.application.TbacHandler;
import cn.sh.ideal.iam.permission.tbac.application.impl.CacheableTbacHandler;
import cn.sh.ideal.iam.permission.tbac.dto.resp.SecurityContainerTreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TbacHandler测试接口
 *
 * @author 宋志宗 on 2024/5/19
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/tbac/test")
public class TbacTestController {
    private final TbacHandler tbacHandler;
    private final CacheableTbacHandler cacheableTbacHandler;
    private final SecurityContainerService securityContainerService;

    /** 获取用户在指定安全容器上所有可见的权限ID */
    @GetMapping("/visible_permission_ids")
    public Result<Set<Long>> visiblePermissionIds(@Nullable Long userId,
                                                  @Nullable Long containerId,
                                                  @Nullable Long appId) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(containerId, "containerId");
        Asserts.nonnull(appId, "appId");
        Set<Long> set = tbacHandler.visiblePermissionIds(userId, containerId, appId);
        return Result.success(set);
    }

    /** 获取用户拥有指定权限的容器id列表 */
    @GetMapping("/authority_container_ids")
    public Result<Set<Long>> authorityContainerIds(@Nullable Long userId,
                                                   @Nullable String authority,
                                                   @Nullable Long baseContainerId) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(authority, "authority");
        Set<Long> set = tbacHandler.authorityContainerIds(userId, authority, baseContainerId);
        return Result.success(set);
    }

    /** 过滤用户在指定安全容器上有权限的权限ID列表 */
    @GetMapping("/container_permission_ids")
    public Result<Set<Long>> containerPermissionIds(@Nullable Long userId,
                                                    @Nullable Long containerId,
                                                    @Nullable String permissionIds) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(containerId, "containerId");
        Asserts.nonnull(permissionIds, "permissionIds");
        String[] split = StringUtils.split(permissionIds, ",");
        Set<Long> permissionIdSet = Arrays.stream(split).map(Long::parseLong).collect(Collectors.toSet());
        Set<Long> set = tbacHandler.containerPermissionIds(userId, containerId, permissionIdSet);
        return Result.success(set);
    }

    /** 批量过滤用户在指定安全容器上有权限的权限ID列表 */
    @GetMapping("/container_permission_ids/batch")
    public Result<Map<Long, Set<Long>>> containerPermissionIds(@Nullable Long userId,
                                                               @Nullable String containerIds,
                                                               @Nullable String permissionIds) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(containerIds, "containerIds");
        Asserts.nonnull(permissionIds, "permissionIds");
        String[] containerIdSplit = StringUtils.split(containerIds, ",");
        Set<Long> containerIdSet = Arrays.stream(containerIdSplit).map(Long::parseLong).collect(Collectors.toSet());

        String[] permissionIdSplit = StringUtils.split(permissionIds, ",");
        Set<Long> permissionIdSet = Arrays.stream(permissionIdSplit).map(Long::parseLong).collect(Collectors.toSet());
        Map<Long, Set<Long>> map = tbacHandler.containerPermissionIds(userId, containerIdSet, permissionIdSet);
        return Result.success(map);
    }

    /** 判断用户在指定安全容器上是否拥有指定权限 */
    @GetMapping("has_authority")
    public Result<Boolean> hasAuthority(@Nullable Long userId,
                                        @Nullable Long containerId,
                                        @Nullable String authority) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(containerId, "containerId");
        Asserts.nonnull(authority, "authority");
        boolean hasAuthority = tbacHandler.hasAuthority(userId, containerId, authority);
        return Result.success(hasAuthority);
    }

    /** 判断用户在指定安全容器上是否拥有任一权限 */
    @GetMapping("has_any_authority")
    public Result<Boolean> hasAnyAuthority(@Nullable Long userId,
                                           @Nullable Long containerId,
                                           @Nullable String authorities) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(containerId, "containerId");
        Asserts.nonnull(authorities, "authorities");
        String[] authoritySplit = StringUtils.split(authorities, ",");
        Set<String> authoritySet = Arrays.stream(authoritySplit).collect(Collectors.toSet());
        boolean hasAuthority = tbacHandler.hasAnyAuthority(userId, containerId, authoritySet);
        return Result.success(hasAuthority);
    }

    /**
     * 判断用户是否拥有API接口的访问权限
     */
    @GetMapping("/has_api_permission")
    public Result<Boolean> hasApiPermission(@Nullable Long userId,
                                            @Nullable Long containerId,
                                            @Nullable String method,
                                            @Nullable String path) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(containerId, "containerId");
        Asserts.nonnull(method, "method");
        Asserts.nonnull(path, "path");
        boolean hasApiPermission = tbacHandler.hasApiPermission(userId, containerId, method, path);
        return Result.success(hasApiPermission);
    }

    /** 判断是否需要mfa验证 */
    @GetMapping("/need_mfa")
    public Result<Boolean> needMfa(@Nullable Long userId,
                                   @Nullable Long containerId,
                                   @Nullable Long permissionId) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(containerId, "containerId");
        Asserts.nonnull(permissionId, "permissionId");
        return Result.success(tbacHandler.needMfa(userId, containerId, permissionId));
    }

    /** 更新用户缓存刷新时间 */
    @PostMapping("/update_user_auth_latest_refresh_timestamp")
    public Result<Void> updateUserAuthLatestRefreshTimestamp(@Nullable Long userId) {
        Asserts.nonnull(userId, "userId");
        long currentTimeMillis = System.currentTimeMillis();
        cacheableTbacHandler.updateUserAuthLatestRefreshTimestamp(userId, currentTimeMillis);
        return Result.success();
    }


    /** 获取用户可见安全容器树 */
    @GetMapping("/visible_container_tree")
    public ListResult<SecurityContainerTreeNode> visibleContainerTree(@Nullable Long userId,
                                                                      @Nullable String authority) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(authority, "authority");
        List<SecurityContainerTreeNode> tree = securityContainerService.visibleContainerTree(userId, authority);
        return ListResult.of(tree);
    }


    /** 获取用户在指定安全容器之上的所有可见父容器 */
    @GetMapping("/visible_container_parent_tree")
    public ListResult<SecurityContainerTreeNode> visibleContainerParentTree(@Nullable Long userId,
                                                                            @Nullable Long containerId,
                                                                            @Nullable String authority) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(authority, "authority");
        Asserts.nonnull(containerId, "containerId");
        List<SecurityContainerTreeNode> tree = securityContainerService
                .visibleContainerParentTree(userId, containerId, authority);
        return ListResult.of(tree);
    }
}
