package cn.sh.ideal.iam.authorization.standard.domain.model;

import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.authorization.core.type.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Slf4j
@RequiredArgsConstructor
public class BearerAuthorization implements Authorization {
    private static final String TYPE = "Bearer";
    private static final String TOKEN_PREFIX = TYPE + " ";
    private long accessId;
    @Nonnull
    private final String visibleToken;

    @Override
    public boolean support(@Nonnull String authorization) {
        return StringUtils.startsWith(authorization, TOKEN_PREFIX);
    }
}
