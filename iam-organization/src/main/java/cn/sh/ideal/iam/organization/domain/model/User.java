package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface User {

    /** 主键 */
    Long getId();

    @Nonnull
    String getPlatform();

    /** 所属租户ID */
    @Nonnull
    Long getTenantId();

    /** 安全容器ID */
    @Nullable
    Long getContainerId();

    /** 姓名 */
    @Nonnull
    String getName();

    /** 账号 */
    @Nullable
    String getAccount();

    /** 获取手机号 */
    @Nullable
    String getPhone();

    /** 获取邮箱 */
    @Nullable
    String getEmail();

    /** 获取语言 */
    @Nullable
    String getLanguage();

    /** 密码 */
    @Nonnull
    String getPassword();

    /** 密码修改时间 */
    long getPasswordTime();

    /** 是否被锁定 */
    boolean isBlocked();
}
