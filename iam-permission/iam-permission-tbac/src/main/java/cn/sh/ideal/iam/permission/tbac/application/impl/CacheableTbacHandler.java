package cn.sh.ideal.iam.permission.tbac.application.impl;

import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.sh.ideal.iam.organization.domain.model.SecurityContainerCache;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionCache;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;

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
    private static final Cache<Long, PermissionAssignsCacheWrapper> PERMISSION_ASSIGN_CACHE =
            Caffeine.newBuilder().maximumSize(2000).expireAfterWrite(LOCAL_CACHE_TIMEOUT).build();
    private final cn.idealio.framework.cache.Cache<Long, Long> userAuthLatestRefreshTimestampCache;


    public CacheableTbacHandler(CacheFactory cacheFactory,
                                UserRepository userRepository,
                                PermissionCache permissionCache,
                                SecurityContainerCache securityContainerCache,
                                PermissionAssignRepository permissionAssignRepository) {
        super(userRepository, permissionCache, securityContainerCache, permissionAssignRepository);
        this.userAuthLatestRefreshTimestampCache = cacheFactory
                .<Long, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(USER_AUTH_REFRESH_TIME_CACHE_TIMEOUT)
                .build("iam:tbac:user_auth_refresh_timestamp");
    }

    @Override
    protected List<PermissionAssign> getAllAssigns(long userId) {
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

    public void updateUserAuthLatestRefreshTimestamp(long userId, long timestamp) {
        userAuthLatestRefreshTimestampCache.put(userId, timestamp);
        PERMISSION_ASSIGN_CACHE.invalidate(userId);
    }

    @Nullable
    private Long getUserAuthLatestRefreshTimestamp(long userId) {
        return userAuthLatestRefreshTimestampCache.getIfPresent(userId);
    }

    private record PermissionAssignsCacheWrapper(long cacheTimestamp,
                                                 @Nonnull List<PermissionAssign> assigns) {
    }

}
