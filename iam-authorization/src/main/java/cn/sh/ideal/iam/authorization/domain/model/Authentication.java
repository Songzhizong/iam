package cn.sh.ideal.iam.authorization.domain.model;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class Authentication {

    /** 用户ID */
    private long userId;

    /** 用户姓名 */
    @Nullable
    private String name;

    /** 用户登录账号 */
    @Nullable
    private String account;

    /** 用户所属租户ID */
    private long tenantId;

    /** 登录时间戳, 毫秒 */
    private long loginTime;
}
