package cn.sh.ideal.iam.factor.core.constant;

/**
 * @author 宋志宗 on 2024/5/29
 */
public enum FactorProvider {
    /** 短信验证码 */
    SMS,
    /** 邮件验证码 */
    EMAIL,
    /** 手机令牌(Time-based One-Time Password) */
    TOTP,
    /** 一次性恢复码 */
    RECOVERY_CODE,
}
