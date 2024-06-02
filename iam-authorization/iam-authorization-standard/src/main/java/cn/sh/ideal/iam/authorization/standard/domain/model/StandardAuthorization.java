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
@SuppressWarnings("ClassCanBeRecord")
@Slf4j
@Getter
public class StandardAuthorization implements Authorization {
    private static final String TYPE = "Standard";
    private static final String TOKEN_PREFIX = TYPE + " ";
    private static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length();
    @SuppressWarnings("SpellCheckingInspection")
    private static final Algorithm ALGORITHM = Algorithm.HMAC256("MWKdQ7jtgy!q3N7Nz3wD7m2iHxaTG.rXhAQFpLbv!EGJYxe3THLE.Q7tMXRyRqeM");
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();

    @Nonnull
    private final Long accessId;
    @Nonnull
    private final String visibleToken;

    public StandardAuthorization(@Nonnull Long accessId, @Nonnull String visibleToken) {
        this.accessId = accessId;
        this.visibleToken = visibleToken;
    }

    public static boolean supports(@Nonnull String authorization) {
        return StringUtils.startsWith(authorization, TOKEN_PREFIX);
    }

    @Nonnull
    public static StandardAuthorization create(@Nonnull Long accessId) {
        String accessToken = JWT.create().withAudience(String.valueOf(accessId)).sign(ALGORITHM);
        return new StandardAuthorization(accessId, accessToken);
    }

    @Nonnull
    public static StandardAuthorization read(@Nonnull String authorization) {
        if (!StandardAuthorization.supports(authorization)) {
            log.warn("StandardAuthorization无法解析此授权头: {}", authorization);
            throw new IllegalArgumentException("Invalid authorization for StandardAuthorization");
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
        Long accessId = Long.valueOf(accessIdString);
        return new StandardAuthorization(accessId, visibleToken);
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
