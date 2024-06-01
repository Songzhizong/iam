package cn.sh.ideal.iam.common.constant;

/**
 * @author 宋志宗 on 2023/3/22
 */
public interface IamGuides {

    /**
     * 请求缺少租户ID
     */
    String MESSING_TENANT_ID = "iam.messing_tenant_id";

    /**
     * 没有指定租户的访问权限
     */
    String TENANT_FORBIDDEN = "iam.tenant_forbidden";

    /**
     * 租户已被冻结
     */
    String TENANT_BLOCKED = "iam.tenant_blocked";

    /**
     * 账户已被冻结
     */
    String USER_BLOCKED = "iam.user_blocked";

    /**
     * 用户名或密码错误
     */
    String USERNAME_OR_PASSWORD_INCORRECT = "iam.username_or_password_incorrect";

    /**
     * 敏感操作
     */
    String SENSITIVE_OPERATION = "iam.sensitive_operation";

}
