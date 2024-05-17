package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.idealio.framework.util.data.hibernate.JpaIDGenerator;
import cn.sh.ideal.iam.permission.tbac.domain.model.UserGroupAssign;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class UserGroupAssignDO implements UserGroupAssign {
    public static final String TABLE_NAME = "iam_tbac_user_group_assign";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(generator = TABLE_NAME)
    @GenericGenerator(name = TABLE_NAME, type = JpaIDGenerator.class)
    private Long id = null;

    @Comment("应用ID")
    @Column(nullable = false, name = "app_id_")
    private long appId;

    @Comment("用户组所属租户ID")
    @Column(nullable = false, name = "tenant_id_")
    private long tenantId;

    @Comment("用户组ID")
    @Column(nullable = false, name = "user_group_id_")
    private long userGroupId;

    @Comment("安全容器ID")
    @Column(nullable = false, name = "container_id_")
    private long containerId;

    @Comment("权限组ID")
    @Column(nullable = false, name = "permission_group_id_")
    private long permissionGroupId;

    @Comment("是否继承, 组下有可继承的权限时为true")
    @Column(nullable = false, name = "extend_")
    private boolean extend = true;
}
