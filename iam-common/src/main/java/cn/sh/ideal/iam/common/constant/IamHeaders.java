package cn.sh.ideal.iam.common.constant;

import cn.idealio.framework.transmission.IdealHeaders;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Http头
 *
 * @author 宋志宗 on 2022/8/16
 */
public interface IamHeaders {

    /**
     * 平台编码, String
     */
    @Nonnull
    String PLATFORM = "x-platform";

    /**
     * 是否多租户平台, Boolean
     */
    @Nonnull
    String MULTI_TENANT = "x-multi-tenant";

    /**
     * 用户id头, Long
     */
    @Nonnull
    String USER_ID = "x-user-id";

    /**
     * 用户姓名
     */
    @Nonnull
    String USER_NAME = "x-user-name";

    /**
     * 用户登录账号
     */
    @Nonnull
    String ACCOUNT = "x-user-account";

    /**
     * 用户归属租户ID
     */
    @Nonnull
    String USER_TENANT_ID = "x-user-tenant-id";

    /**
     * 当前访问租户ID
     */
    @Nonnull
    String TENANT_ID = IdealHeaders.TENANT_ID;

    @Nonnull
    String CLIENT_TOKEN = "x-auth-client-token";

    @Nonnull
    static String encode(@Nonnull String headerValue) {
        byte[] bytes = headerValue.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Nonnull
    static String decode(@Nonnull String encodedValue) {
        byte[] decode = Base64.getDecoder().decode(encodedValue);
        return new String(decode, StandardCharsets.UTF_8);
    }
}
