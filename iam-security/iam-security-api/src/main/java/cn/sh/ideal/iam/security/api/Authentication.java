package cn.sh.ideal.iam.security.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface Authentication {

    /** 获取用户ID */
    long userId();

    /** 获取用户所属租户ID */
    @Nonnull
    Long tenantId();

    /** 获取用户姓名 */
    @Nullable
    String name();

    /** 获取登录账号 */
    @Nullable
    String account();
}
