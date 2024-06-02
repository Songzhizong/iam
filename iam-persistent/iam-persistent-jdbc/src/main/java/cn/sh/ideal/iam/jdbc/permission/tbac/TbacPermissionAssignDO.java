package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.idealio.framework.util.data.hibernate.annotations.JpaIdentityGenerator;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Getter
@Setter
@Entity(name = TbacPermissionAssignDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = TbacPermissionAssignDO.TABLE_NAME,
        indexes = {
                @Index(name = "uidx01_" + TbacPermissionAssignDO.TABLE_NAME,
                        columnList = "container_id_,user_group_id_,permission_id_", unique = true),
                @Index(name = "idx01_" + TbacPermissionAssignDO.TABLE_NAME, columnList = "app_id_"),
                @Index(name = "idx02_" + TbacPermissionAssignDO.TABLE_NAME, columnList = "permission_id_"),
                @Index(name = "idx03_" + TbacPermissionAssignDO.TABLE_NAME, columnList = "user_group_id_"),
                @Index(name = "idx04_" + TbacPermissionAssignDO.TABLE_NAME, columnList = "permission_item_id_"),
                @Index(name = "idx05_" + TbacPermissionAssignDO.TABLE_NAME, columnList = "permission_group_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class TbacPermissionAssignDO implements PermissionAssign {
    public static final String TABLE_NAME = "iam_tbac_permission_assign";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @JpaIdentityGenerator(name = TABLE_NAME)
    private Long id = null;

    @Nonnull
    @Comment("应用ID")
    @Column(nullable = false, name = "app_id_")
    private Long appId = -1L;

    @Nonnull
    @Comment("安全容器ID")
    @Column(nullable = false, name = "container_id_")
    private Long containerId = -1L;

    @Nonnull
    @Comment("用户组ID")
    @Column(nullable = false, name = "user_group_id_")
    private Long userGroupId = -1L;

    @Nonnull
    @Comment("权限组ID")
    @Column(nullable = false, name = "permission_group_id_")
    private Long permissionGroupId = -1L;

    @Nonnull
    @Comment("权限项ID")
    @Column(nullable = false, name = "permission_item_id_")
    private Long permissionItemId = -1L;

    @Nonnull
    @Comment("权限点ID")
    @Column(nullable = false, name = "permission_id_")
    private Long permissionId = -1L;

    @Comment("是否授权")
    @Column(nullable = false, name = "assigned_")
    private boolean assigned = true;

    @Comment("是否可被继承, 只有授权可继承")
    @Column(nullable = false, name = "inheritable_")
    private boolean inheritable = true;

    @Comment("是否需要双因素认证")
    @Column(nullable = false, name = "mfa_")
    private boolean mfa = false;

    @Nonnull
    public static TbacPermissionAssignDO create(@Nonnull Long containerId,
                                                @Nonnull Long userGroupId,
                                                boolean assign,
                                                boolean inheritable,
                                                boolean mfa,
                                                @Nonnull Permission permission) {
        TbacPermissionAssignDO permissionAssignDO = new TbacPermissionAssignDO();
        permissionAssignDO.setAppId(permission.getAppId());
        permissionAssignDO.setContainerId(containerId);
        permissionAssignDO.setUserGroupId(userGroupId);
        permissionAssignDO.setPermissionGroupId(permission.getGroupId());
        permissionAssignDO.setPermissionItemId(permission.getItemId());
        permissionAssignDO.setPermissionId(permission.getId());
        permissionAssignDO.setAssigned(assign);
        permissionAssignDO.setInheritable(inheritable);
        permissionAssignDO.setMfa(mfa);
        return permissionAssignDO;
    }
}
