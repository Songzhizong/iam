package cn.sh.ideal.iam.infrastructure.user;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface UserDetail {

    /** 获取用户ID */
    long getId();

    /** 获取用户归属平台 */
    @Nonnull
    String getPlatform();

    long getTenantId();

    @Nonnull
    String getName();

    /** 获取登录账号 */
    @Nullable
    String getAccount();

    /** 获取手机号码 */
    @Nullable
    String getPhone();

    /** 获取邮箱地址 */
    @Nullable
    String getEmail();

    /** 账号是否被锁定 */
    boolean isBlocked();

    /** 账号是否已过期 */
    boolean isAccountExpired();

    /** 密码是否已过期 */
    boolean isPasswordExpired();
}
