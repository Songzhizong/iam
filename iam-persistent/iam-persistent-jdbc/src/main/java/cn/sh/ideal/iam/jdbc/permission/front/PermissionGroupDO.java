package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.annotations.ManualIdentityGenerator;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionGroup;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionGroupArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionGroupInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Getter
@Setter
@Entity(name = PermissionGroupDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = PermissionGroupDO.TABLE_NAME,
        indexes = {
                @Index(name = "idx01_" + PermissionGroupDO.TABLE_NAME, columnList = "app_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class PermissionGroupDO implements PermissionGroup {
    public static final String TABLE_NAME = "iam_permission_group";

    @Id
    @Nonnull
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @ManualIdentityGenerator(name = TABLE_NAME)
    private Long id = -1L;

    @Nonnull
    @Comment("应用ID")
    @Column(nullable = false, name = "app_id_")
    private Long appId = -1L;

    @Nonnull
    @Comment("名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Comment("是否启用")
    @Column(nullable = false, name = "enabled_")
    private boolean enabled = true;

    @Comment("排序值")
    @Column(nullable = false, name = "order_num_")
    private int orderNum = 0;

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @Nonnull
    public static PermissionGroupDO create(@Nonnull Long id,
                                           @Nonnull CreatePermissionGroupArgs args) {
        Long appId = args.getAppId();
        String name = args.getName();
        Boolean enabled = args.getEnabled();
        Integer orderNum = args.getOrderNum();
        Asserts.nonnull(appId, "所属应用ID为空");
        Asserts.notBlank(name, "权限分组名称为空");
        if (enabled == null) {
            enabled = true;
        }
        PermissionGroupDO permissionGroupDO = new PermissionGroupDO();
        permissionGroupDO.setId(id);
        permissionGroupDO.setAppId(appId);
        permissionGroupDO.setName(name);
        permissionGroupDO.setEnabled(enabled);
        permissionGroupDO.setOrderNum(orderNum);
        return permissionGroupDO;
    }

    @Nonnull
    public static PermissionGroupDO ofInfo(@Nonnull PermissionGroupInfo info) {
        PermissionGroupDO permissionGroupDO = new PermissionGroupDO();
        permissionGroupDO.setId(info.getId());
        permissionGroupDO.setAppId(info.getAppId());
        permissionGroupDO.setName(info.getName());
        permissionGroupDO.setEnabled(info.isEnabled());
        permissionGroupDO.setOrderNum(info.getOrderNum());
        return permissionGroupDO;
    }

    public void setOrderNum(@Nullable Integer orderNum) {
        if (orderNum == null) {
            orderNum = 0;
        }
        this.orderNum = orderNum;
    }
}
