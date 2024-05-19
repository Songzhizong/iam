package cn.sh.ideal.iam.infrastructure.constant;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface AuditConstants {

    // ============================== 租户 ~ ~ ~

    /** 租户 */
    String TENANT = "iam.tenant";

    /** 新增租户 */
    String CREATE_TENANT = "iam:tenant:create";

    /** 更新租户 */
    String UPDATE_TENANT = "iam:tenant:update";

    /** 删除租户 */
    String DELETE_TENANT = "iam:tenant:delete";


    // ============================== 用户 ~ ~ ~
    /** 用户 */
    String USER = "iam.user";

    /** 新增用户 */
    String CREATE_USER = "iam.user.create";


    // ============================== 用户组 ~ ~ ~
    /** 用户组 */
    String USER_GROUP = "iam.user_group";

    /** 新增用户组 */
    String CREATE_USER_GROUP = "iam.user_group.create";

    /** 删除用户组 */
    String DELETE_USER_GROUP = "iam.user_group.delete";

    // ============================== 安全容器 ~ ~ ~

    String SECURITY_CONTAINER = "iam.security_container";

    String CREATE_SECURITY_CONTAINER = "iam:security_container:create";

    String RENAME_SECURITY_CONTAINER = "iam:security_container:rename";

    String MOVE_SECURITY_CONTAINER = "iam:security_container:move";
    String DELETE_SECURITY_CONTAINER = "iam:security_container:delete";


    // ============================== 权限管理 ~ ~ ~
    /** 权限管理 */
    String AUTHORITY_MANAGEMENT = "iam.authority_management";
}
