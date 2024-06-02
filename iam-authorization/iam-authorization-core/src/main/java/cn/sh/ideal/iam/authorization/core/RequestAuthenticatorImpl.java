package cn.sh.ideal.iam.authorization.core;

import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.net.http.HttpHeaders;
import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.adapter.RequestAuthenticator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestAuthenticatorImpl implements RequestAuthenticator {
    private static final String AUTHORIZATION_LOWER_CASE = HttpHeaders.AUTHORIZATION.toLowerCase();
    private final List<AuthenticationRetriever> authenticationRetrievers;

    @Nullable
    @Override
    public Authentication authenticate(@Nonnull HttpServletRequest request) {
        String authorization = getAuthorization(request);
        if (StringUtils.isBlank(authorization)) {
            log.info("缺少 Authorization 请求头");
            return null;
        }
        for (AuthenticationRetriever authenticationRetriever : authenticationRetrievers) {
            if (authenticationRetriever.supports(authorization)) {
                return authenticationRetriever.retrieve(authorization);
            }
        }
        log.warn("不支持的 Authorization 类型: {}", authorization);
        return null;
    }

    @Nullable
    private String getAuthorization(@Nonnull HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            String queryString = request.getQueryString();
            MultiValueMap<String, String> queryParams = toQueryParams(queryString);
            authorization = queryParams.getFirst(AUTHORIZATION_LOWER_CASE);
            if (StringUtils.isBlank(authorization)) {
                authorization = queryParams.getFirst(HttpHeaders.AUTHORIZATION);
            }
        }
        return authorization;
    }

    @Nonnull
    private MultiValueMap<String, String> toQueryParams(@Nullable String queryString) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (StringUtils.isBlank(queryString)) {
            return map;
        }
        String[] split = StringUtils.split(queryString, "&");
        for (String s : split) {
            String[] split1 = StringUtils.split(s, "=", 2);
            if (split1.length != 2) {
                continue;
            }
            map.add(split1[0], split1[1]);
        }
        return map;
    }
}
