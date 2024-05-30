package cn.sh.ideal.iam.authorization.standard.application;

import cn.idealio.framework.cache.CacheFactory;
import cn.idealio.framework.cache.serialize.LongSerializer;
import cn.idealio.framework.concurrent.Asyncs;
import cn.idealio.framework.lock.GlobalLock;
import cn.idealio.framework.lock.GlobalLockFactory;
import cn.sh.ideal.iam.authorization.standard.configure.AuthorizationStandardProperties;
import cn.sh.ideal.iam.authorization.standard.domain.model.*;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
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
    private static final Duration INVALIDATE_CACHE_DELAY = Duration.ofSeconds(2);
    private final String lockValue = UUID.randomUUID().toString();
    private final EntityFactory entityFactory;
    private final GlobalLockFactory globalLockFactory;
    private final AuthorizationStandardProperties properties;
    private final AccessTokenRepository accessTokenRepository;
    private final cn.idealio.framework.cache.Cache<Long, Long> tokenChangeCache;

    public AccessTokenService(@Nonnull CacheFactory cacheFactory,
                              @Nonnull EntityFactory entityFactory,
                              @Nonnull GlobalLockFactory globalLockFactory,
                              @Nonnull AuthorizationStandardProperties properties,
                              @Nonnull AccessTokenRepository accessTokenRepository) {
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
        long accessId = accessToken.getId();
        BearerAuthorization authorization = BearerAuthorization.create(accessId);
        String type = authorization.getType();
        String visibleToken = authorization.getVisibleToken();
        TOKEN_CACHE.put(accessId, new TokenCacheWrapper(accessToken, System.currentTimeMillis()));
        // 如果禁止重复登录, 则清理掉之前的token, 避免一个账号多地登录
        if (!properties.isAllowMultipleLogin()) {
            long userId = userDetail.getId();
            long clientId = authClient.getId();
            Asyncs.executeVirtual(() -> cleanupOutdatedToken(userId, clientId, accessId));
        }
        return VisibleToken.create(type, visibleToken);
    }

    private void cleanupOutdatedToken(long userId, long clientId, long accessId) {
        // TODO 清理过期Token
    }


    public void delete(long accessId) {
        accessTokenRepository.deleteById(accessId);
        invalidate(accessId);
    }

    @Nullable
    public AccessToken get(long accessId) {
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
        long accessId = accessToken.getId();
        String lockKey = "iam:access_token:renewal:" + accessId;
        GlobalLock lock = globalLockFactory.getLock(lockKey, RENEWAL_TIMEOUT);
        boolean tryLock = lock.tryLock(lockValue);
        if (!tryLock) {
            return;
        }
        try {
            AccessToken token = accessTokenRepository.findById(accessId).orElse(null);
            if (token != null && token.renewal()) {
                accessTokenRepository.update(token);
                invalidate(accessId);
            }
        } finally {
            lock.unlock(lockValue);
        }
    }

    public void invalidate(long accessId) {
        TOKEN_CACHE.invalidate(accessId);
        Asyncs.execAndDelayVirtual(INVALIDATE_CACHE_DELAY, () -> tokenChangeCache.invalidate(accessId));
    }

    @Nonnull
    private TokenCacheWrapper getTokenCacheWrapper(long accessId) {
        return TOKEN_CACHE.get(accessId, key -> {
            long currentTimeMillis = System.currentTimeMillis();
            AccessToken token = accessTokenRepository.findById(accessId).orElse(null);
            return new TokenCacheWrapper(token, currentTimeMillis);
        });
    }

    public record TokenCacheWrapper(@Nullable AccessToken accessToken, long cachedTime) {
    }
}
