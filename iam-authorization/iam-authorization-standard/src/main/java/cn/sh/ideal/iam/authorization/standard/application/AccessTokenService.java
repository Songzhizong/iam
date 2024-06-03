package cn.sh.ideal.iam.authorization.standard.application;

import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.concurrent.Asyncs;
import cn.idealio.framework.lock.GlobalLock;
import cn.idealio.framework.lock.GlobalLockFactory;
import cn.idealio.framework.transmission.Paging;
import cn.sh.ideal.iam.authorization.standard.configure.AuthorizationStandardProperties;
import cn.sh.ideal.iam.authorization.standard.domain.model.*;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import cn.sh.ideal.iam.infrastructure.user.UserLastActiveRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Slf4j
@Service
public class AccessTokenService {
    private static final Cache<Long, TokenCacheWrapper> TOKEN_CACHE = Caffeine.newBuilder()
            .maximumSize(1000).expireAfterWrite(Duration.ofMinutes(30)).build();
    private static final Duration RENEWAL_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration CLEANUP_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration INVALIDATE_CACHE_DELAY = Duration.ofSeconds(2);
    private static final Paging CLEAN_PAGING = Paging.of(1, 50).asc(AccessToken::getId);
    private final String lockCertificate = UUID.randomUUID().toString();
    private final GlobalLockFactory globalLockFactory;
    private final AuthorizationStandardProperties properties;
    private final UserLastActiveRepository userLastActiveRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final StandardAuthorizationEntityFactory entityFactory;
    private final cn.idealio.framework.cache.Cache<Long, Long> tokenChangeCache;

    public AccessTokenService(@Nonnull CacheFactory cacheFactory,
                              @Nonnull GlobalLockFactory globalLockFactory,
                              @Nonnull AuthorizationStandardProperties properties,
                              @Nonnull UserLastActiveRepository userLastActiveRepository,
                              @Nonnull AccessTokenRepository accessTokenRepository,
                              @Nonnull StandardAuthorizationEntityFactory entityFactory) {
        this.userLastActiveRepository = userLastActiveRepository;
        this.properties = properties;
        this.entityFactory = entityFactory;
        this.globalLockFactory = globalLockFactory;
        this.accessTokenRepository = accessTokenRepository;
        this.tokenChangeCache = cacheFactory.<Long, Long>newBuilder(LongSerializer.instance())
                .expireAfterWrite(Duration.ofHours(2)).build("iam:access_token:change_time");
    }

    @Nonnull
    public VisibleToken generate(@Nonnull AuthClient authClient,
                                 @Nonnull UserDetail userDetail) {
        long sessionTimeout = properties.getSessionTimeout().toMillis();
        AccessToken accessToken = entityFactory.accessToken(authClient, userDetail, sessionTimeout);
        accessToken = accessTokenRepository.insert(accessToken);
        Long accessId = accessToken.getId();
        StandardAuthorization authorization = StandardAuthorization.create(accessId);
        String type = authorization.getType();
        String visibleToken = authorization.getVisibleToken();
        TOKEN_CACHE.put(accessId, new TokenCacheWrapper(accessToken, System.currentTimeMillis()));
        Long userId = userDetail.getId();
        // 如果禁止重复登录, 则清理掉之前的token, 避免一个账号多地登录
        if (!properties.isAllowMultipleLogin()) {
            Long clientId = authClient.getId();
            Asyncs.executeVirtual(() -> cleanupOutdatedToken(userId, clientId));
        }
        // 更新用户最近活跃时间
        userLastActiveRepository.updateLastActiveTime(userId);
        return VisibleToken.create(type, visibleToken);
    }

    private void cleanupOutdatedToken(@Nonnull Long userId,
                                      @Nonnull Long clientId) {

        String lockKey = "iam:access_token:cleanup_outdated:" + clientId + ":" + userId;
        GlobalLock lock = globalLockFactory.getLock(lockKey, CLEANUP_TIMEOUT);
        if (!lock.tryLock(lockCertificate)) {
            return;
        }
        try {
            while (true) {
                List<AccessToken> tokens = accessTokenRepository
                        .findAllByUserIdAndClientId(userId, clientId, CLEAN_PAGING);
                // 删除除了最新的token之外的其他token, 因为是升序查询的, 也就是除了最后一个剩下的全删除
                int size = tokens.size();
                if (size <= 1) {
                    break;
                }
                List<Long> accessIds = tokens.subList(0, size - 1)
                        .stream().map(AccessToken::getId).toList();
                for (Long accessId : accessIds) {
                    invalidate(accessId);
                }
                accessTokenRepository.deleteAllById(accessIds);
                lock.renewal();
            }
        } finally {
            lock.unlock(lockCertificate);
        }
    }


    public void delete(@Nonnull Long accessId) {
        accessTokenRepository.deleteById(accessId);
        invalidate(accessId);
    }

    @Nullable
    public AccessToken get(@Nonnull Long accessId) {
        TokenCacheWrapper wrapper = getTokenCacheWrapper(accessId);
        Long tokenChangeTime = tokenChangeCache.getIfPresent(accessId);
        if (tokenChangeTime != null && tokenChangeTime > wrapper.cachedTime) {
            TOKEN_CACHE.invalidate(accessId);
            wrapper = getTokenCacheWrapper(accessId);
        }
        AccessToken accessToken = wrapper.accessToken();
        if (accessToken == null) {
            log.info("获取Token缓存信息失败, Token不存在: {}", accessId);
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (accessToken.getExpiration() < currentTimeMillis) {
            invalidate(accessId);
            return null;
        }
        Asyncs.executeVirtual(() -> renewal(accessToken));
        return accessToken;
    }

    private void renewal(@Nonnull AccessToken accessToken) {
        if (!accessToken.renewal()) {
            return;
        }
        Long accessId = accessToken.getId();
        String lockKey = "iam:access_token:renewal:" + accessId;
        GlobalLock lock = globalLockFactory.getLock(lockKey, RENEWAL_TIMEOUT);
        boolean tryLock = lock.tryLock(lockCertificate);
        if (!tryLock) {
            return;
        }
        try {
            AccessToken token = accessTokenRepository.findById(accessId).orElse(null);
            if (token != null && token.renewal()) {
                accessTokenRepository.update(token);
                invalidate(accessId);
            }
            // 更新用户最近活跃时间
            Long userId = accessToken.getUserId();
            userLastActiveRepository.updateLastActiveTime(userId);
        } finally {
            lock.unlock(lockCertificate);
        }
    }

    public void invalidate(@Nonnull Long accessId) {
        TOKEN_CACHE.invalidate(accessId);
        Asyncs.execAndDelayVirtual(INVALIDATE_CACHE_DELAY, () -> tokenChangeCache.invalidate(accessId));
    }

    @Nonnull
    private TokenCacheWrapper getTokenCacheWrapper(@Nonnull Long accessId) {
        return TOKEN_CACHE.get(accessId, key -> {
            long currentTimeMillis = System.currentTimeMillis();
            AccessToken token = accessTokenRepository.findById(accessId).orElse(null);
            return new TokenCacheWrapper(token, currentTimeMillis);
        });
    }

    public record TokenCacheWrapper(@Nullable AccessToken accessToken, long cachedTime) {
    }
}
