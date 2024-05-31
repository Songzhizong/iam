package cn.sh.ideal.iam.authorization.standard.domain.model;

import cn.idealio.framework.exception.UnauthorizedException;
import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.authorization.core.Authorization;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Slf4j
@Getter
@SuppressWarnings("ClassCanBeRecord")
public class BearerAuthorization implements Authorization {
    private static final String TYPE = "Bearer";
    private static final String TOKEN_PREFIX = TYPE + " ";
    private static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length();
    @SuppressWarnings("SpellCheckingInspection")
    private static final Algorithm ALGORITHM = Algorithm.HMAC256("MWKdQ7jtgy!q3N7Nz3wD7m2iHxaTG.rXhAQFpLbv!EGJYxe3THLE.Q7tMXRyRqeM");
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();

    private final long accessId;
    @Nonnull
    private final String visibleToken;

    public BearerAuthorization(long accessId, @Nonnull String visibleToken) {
        this.accessId = accessId;
        this.visibleToken = visibleToken;
    }

    public static boolean supports(@Nonnull String authorization) {
        return StringUtils.startsWith(authorization, TOKEN_PREFIX);
    }

    @Nonnull
    public static BearerAuthorization create(long accessId) {
        String accessToken = JWT.create().withAudience(String.valueOf(accessId)).sign(ALGORITHM);
        return new BearerAuthorization(accessId, accessToken);
    }

    @Nonnull
    public static BearerAuthorization read(@Nonnull String authorization) {
        if (!BearerAuthorization.supports(authorization)) {
            log.warn("BearerAuthorization无法解析此授权头: {}", authorization);
            throw new IllegalArgumentException("Invalid authorization for BearerAuthorization");
        }
        String visibleToken = authorization.substring(TOKEN_PREFIX_LENGTH);
        DecodedJWT decode;
        try {
            decode = VERIFIER.verify(visibleToken);
        } catch (JWTVerificationException e) {
            log.info("JWT解析出现异常: {} token: {}", e.getMessage(), visibleToken, e);
            throw new UnauthorizedException("Invalid Authorization");
        }
        List<String> audience = decode.getAudience();
        //noinspection SizeReplaceableByIsEmpty
        if (audience == null || audience.size() < 1) {
            log.info("无效的Authorization: audience为空 token: {}", visibleToken);
            throw new UnauthorizedException("Invalid Authorization");
        }
        String accessIdString = audience.getFirst();
        long accessId = Long.parseLong(accessIdString);
        return new BearerAuthorization(accessId, visibleToken);
    }

    @Nonnull
    public String toAuthorization() {
        return TOKEN_PREFIX + visibleToken;
    }

    @Nonnull
    @Override
    public String getType() {
        return TYPE;
    }
}
