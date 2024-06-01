package cn.sh.ideal.iam.common.util;

import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.common.constant.IamHeaders;
import jakarta.servlet.http.HttpServletRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/31
 */
public final class RequestUtils {

    @Nullable
    public static Long getAppId(@Nonnull HttpServletRequest httpServletRequest) {
        String appIdHeader = httpServletRequest.getHeader(IamHeaders.APP_ID);
        if (StringUtils.isBlank(appIdHeader)) {
            return null;
        }
        return Long.valueOf(appIdHeader);
    }

    @Nullable
    public static Long getTenantId(@Nonnull HttpServletRequest httpServletRequest) {
        String tenantIdHeader = httpServletRequest.getHeader(IamHeaders.TENANT_ID);
        if (StringUtils.isBlank(tenantIdHeader)) {
            return null;
        }
        return Long.valueOf(tenantIdHeader);
    }
}
