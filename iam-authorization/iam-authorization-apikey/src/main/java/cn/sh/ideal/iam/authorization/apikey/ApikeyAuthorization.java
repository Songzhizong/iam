package cn.sh.ideal.iam.authorization.apikey;

import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.authorization.core.type.Authorization;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/29
 */
@RequiredArgsConstructor
public class ApikeyAuthorization implements Authorization {
    private static final String TYPE = "Apikey";
    private static final String TOKEN_PREFIX = TYPE + " ";
    @Nonnull
    private final String token;

    public static boolean support(@Nonnull String authorization) {
        return StringUtils.startsWith(authorization, TOKEN_PREFIX);
    }

    @Nonnull
    public ApikeyAuthorization of(@Nonnull String authorization) {
        return new ApikeyAuthorization(authorization.substring(TOKEN_PREFIX.length()));
    }

    @Override
    public String toString() {
        return TOKEN_PREFIX + token;
    }

    @Nonnull
    @Override
    public String getType() {
        return TYPE;
    }
}
