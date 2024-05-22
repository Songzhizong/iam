package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.lang.Sets;
import cn.idealio.framework.spring.matcher.AlwaysFalseMethodPathMatcher;
import cn.idealio.framework.spring.matcher.MethodPathMatcher;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.tbac.domain.model.AssignedPermission;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignRepository;
import cn.sh.ideal.iam.permission.tbac.domain.model.SecurityContainerCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 支持缓存加速
 *
 * @author 宋志宗 on 2024/5/17
 */
@Slf4j
@Component
public class CacheableTbacHandler extends CachelessTbacHandler {
    private static final Duration LOCAL_CACHE_TIMEOUT = Duration.ofMinutes(30);
    private static final Duration USER_AUTH_REFRESH_TIME_CACHE_TIMEOUT = Duration.ofHours(1);
    private static final Cache<Long, AuthoritiesCacheWrapper> AUTHORITIES_CACHE =
            Caffeine.newBuilder().maximumSize(2000).expireAfterWrite(LOCAL_CACHE_TIMEOUT).build();
    private static final Cache<Long, ApiPermissionCacheWrapper> API_PERMISSION_CACHE =
            Caffeine.newBuilder().maximumSize(2000).expireAfterWrite(LOCAL_CACHE_TIMEOUT).build();
    private static final Cache<Long, PermissionAssignsCacheWrapper> PERMISSION_ASSIGN_CACHE =
            Caffeine.newBuilder().maximumSize(2000).expireAfterWrite(LOCAL_CACHE_TIMEOUT).build();

    private final cn.idealio.framework.cache.Cache<Long, Long> userAuthLatestRefreshTimestampCache;


    public CacheableTbacHandler(@Nonnull CacheFactory cacheFactory,
                                @Nonnull UserRepository userRepository,
                                @Nonnull PermissionCache permissionCache,
                                @Nonnull SecurityContainerCache securityContainerCache,
                                @Nonnull PermissionAssignRepository permissionAssignRepository) {
        super(userRepository, permissionCache, securityContainerCache, permissionAssignRepository);
        this.userAuthLatestRefreshTimestampCache = cacheFactory
                .<Long, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(USER_AUTH_REFRESH_TIME_CACHE_TIMEOUT)
                .build("iam:tbac:user_auth_refresh_timestamp");
    }

    @Nonnull
    @Override
    public List<PermissionAssign> getAllAssigns(long userId) {
        PermissionAssignsCacheWrapper wrapper = PERMISSION_ASSIGN_CACHE.get(userId, k -> {
            List<PermissionAssign> assigns = super.getAllAssigns(userId);
            return new PermissionAssignsCacheWrapper(System.currentTimeMillis(), assigns);
        });
        Long latestRefreshTimestamp = getUserAuthLatestRefreshTimestamp(userId);
        if (latestRefreshTimestamp == null || wrapper.cacheTimestamp() > latestRefreshTimestamp) {
            return wrapper.assigns();
        }
        log.info("用户权限发生变更, 重新加载用户所有权限分配信息缓存");
        PERMISSION_ASSIGN_CACHE.invalidate(userId);
        return PERMISSION_ASSIGN_CACHE.get(userId, k -> {
            List<PermissionAssign> assigns = super.getAllAssigns(userId);
            return new PermissionAssignsCacheWrapper(System.currentTimeMillis(), assigns);
        }).assigns();
    }


    @Override
    public boolean hasAuthority(long userId, long containerId, @Nonnull String authority) {
        Set<String> authorities = getAuthorities(userId, containerId);
        return authorities.contains(authority);
    }

    @Override
    public boolean hasAnyAuthority(long userId, long containerId,
                                   @Nonnull Set<String> authorities) {
        Set<String> userAuthorities = getAuthorities(userId, containerId);
        return Sets.containsAny(userAuthorities, authorities);
    }

    @Nonnull
    private Set<String> getAuthorities(long userId, long containerId) {
        AuthoritiesCacheWrapper wrapper = AUTHORITIES_CACHE.get(userId, key ->
                new AuthoritiesCacheWrapper(System.currentTimeMillis(), new ConcurrentHashMap<>()));
        Long latestRefreshTimestamp = getUserAuthLatestRefreshTimestamp(userId);
        if (latestRefreshTimestamp != null && wrapper.cacheTimestamp() < latestRefreshTimestamp) {
            wrapper = new AuthoritiesCacheWrapper(System.currentTimeMillis(), new ConcurrentHashMap<>());
            AUTHORITIES_CACHE.put(userId, wrapper);
            log.info("用户权限发生变更, 清空用户所有权限标识缓存");
        }
        return wrapper.authoritiesMap().computeIfAbsent(containerId, k -> {
            Collection<AssignedPermission> assignedPermissions =
                    getAssignedPermissions(userId, containerId, true);
            if (assignedPermissions.isEmpty()) {
                return Set.of();
            }
            Set<String> authorities = new HashSet<>();
            for (AssignedPermission assignedPermission : assignedPermissions) {
                Permission permission = assignedPermission.getPermission();
                Set<String> permissionAuthorities = permission.getAuthorities();
                if (permissionAuthorities.isEmpty()) {
                    continue;
                }
                authorities.addAll(permissionAuthorities);
            }
            return authorities;
        });
    }

    @Override
    public boolean hasApiPermission(long userId, long containerId,
                                    @Nonnull String method, @Nonnull String path) {
        ApiPermissionCacheWrapper wrapper = API_PERMISSION_CACHE.get(userId, key ->
                new ApiPermissionCacheWrapper(System.currentTimeMillis(), new ConcurrentHashMap<>()));
        Long latestRefreshTimestamp = getUserAuthLatestRefreshTimestamp(userId);
        if (latestRefreshTimestamp != null && wrapper.cacheTimestamp() < latestRefreshTimestamp) {
            wrapper = new ApiPermissionCacheWrapper(System.currentTimeMillis(), new ConcurrentHashMap<>());
            API_PERMISSION_CACHE.put(userId, wrapper);
            log.info("用户权限发生变更, 清空用户所有API权限验证器缓存");
        }
        MethodPathMatcher matcher = getMethodPathMatcher(userId, containerId, wrapper);
        return matcher.matches(method, path);
    }

    @Nonnull
    private MethodPathMatcher getMethodPathMatcher(long userId, long containerId,
                                                   @Nonnull ApiPermissionCacheWrapper wrapper) {
        ConcurrentMap<Long, MethodPathMatcher> map = wrapper.matcherMap();
        return map.computeIfAbsent(containerId, key -> {
            Collection<AssignedPermission> assignedPermissions =
                    getAssignedPermissions(userId, containerId, true);
            if (assignedPermissions.isEmpty()) {
                return AlwaysFalseMethodPathMatcher.getInstance();
            }
            Set<String> strategies = new HashSet<>();
            for (AssignedPermission assignedPermission : assignedPermissions) {
                Permission permission = assignedPermission.getPermission();
                Set<String> apiPatterns = permission.getApiPatterns();
                Set<String> specificApis = permission.getSpecificApis();
                strategies.addAll(apiPatterns);
                strategies.addAll(specificApis);
            }
            return MethodPathMatcher.create(strategies);
        });
    }

    public void updateUserAuthLatestRefreshTimestamp(long userId, long timestamp) {
        userAuthLatestRefreshTimestampCache.put(userId, timestamp);
    }

    @Nullable
    private Long getUserAuthLatestRefreshTimestamp(long userId) {
        return userAuthLatestRefreshTimestampCache.getIfPresent(userId);
    }

    /**
     * 权限分配缓存包装器
     *
     * @param cacheTimestamp 缓存产生时的毫秒时间戳
     * @param assigns        权限分配列表
     */
    private record PermissionAssignsCacheWrapper(long cacheTimestamp,
                                                 @Nonnull List<PermissionAssign> assigns) {
    }


    /**
     * API权限匹配缓存包装器
     *
     * @param cacheTimestamp 缓存产生时的毫秒时间戳
     * @param matcherMap     containerId -> MethodPathMatcher
     */
    private record ApiPermissionCacheWrapper(long cacheTimestamp,
                                             @Nonnull ConcurrentMap<Long, MethodPathMatcher> matcherMap) {
    }

    /**
     * 权限标识缓存包装器
     *
     * @param cacheTimestamp 缓存产生时的毫秒时间戳
     * @param authoritiesMap containerId -> 权限标识列表
     */
    private record AuthoritiesCacheWrapper(long cacheTimestamp,
                                           @Nonnull ConcurrentMap<Long, Set<String>> authoritiesMap) {
    }

}
