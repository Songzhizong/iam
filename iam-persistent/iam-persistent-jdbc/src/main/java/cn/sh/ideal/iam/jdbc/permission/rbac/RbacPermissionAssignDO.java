package cn.sh.ideal.iam.jdbc.permission.rbac;

import cn.idealio.framework.util.data.hibernate.annotations.JpaIdentityGenerator;
import cn.idealio.framework.util.data.jpa.LongSetGzipConverter;
import cn.sh.ideal.iam.permission.rbac.domain.model.PermissionAssign;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.LongVarbinaryJdbcType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/16
 */

@Getter
@Setter
@Entity(name = RbacPermissionAssignDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = RbacPermissionAssignDO.TABLE_NAME,
        indexes = {
                @Index(name = "uidx01_" + RbacPermissionAssignDO.TABLE_NAME,
                        columnList = "user_group_id_,tenant_id_", unique = true),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class RbacPermissionAssignDO implements PermissionAssign {
    public static final String TABLE_NAME = "iam_rbac_permission_assign";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @JpaIdentityGenerator(name = TABLE_NAME)
    private Long id = null;

    @Comment("应用ID")
    @Column(nullable = false, name = "app_id_")
    private long appId;

    @Comment("租户ID")
    @Column(nullable = false, name = "tenant_id_")
    private long tenantId;

    @Comment("用户组ID")
    @Column(nullable = false, name = "user_group_id_")
    private long userGroupId;

    @Nonnull
    @Comment("分配的权限ID列表")
    @JdbcType(LongVarbinaryJdbcType.class)
    @Convert(converter = LongSetGzipConverter.class)
    @Column(nullable = false, name = "permission_ids_")
    private Set<Long> permissionIds = new LinkedHashSet<>();

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @CreatedDate
    @Column(nullable = false, name = "created_time_")
    private long createdTime = 0;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_time_")
    private long updatedTime = 0;
}
