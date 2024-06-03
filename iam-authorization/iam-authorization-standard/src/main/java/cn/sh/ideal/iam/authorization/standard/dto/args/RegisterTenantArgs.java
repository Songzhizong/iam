package cn.sh.ideal.iam.authorization.standard.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class RegisterTenantArgs {

    /** 组织名称 */
    @Nullable
    private String name;

    /** 登录账号 */
    @Nullable
    private String account;

    /**
     * 登录密码
     *
     * @required
     */
    @Nullable
    private String password;

    /** 手机号码 */
    @Nullable
    private String phone;

    /** 手机验证码 */
    @Nullable
    private String phoneCode;

    /** 邮箱地址 */
    @Nullable
    private String email;

    /** 邮箱验证码 */
    @Nullable
    private String emailCode;
}
