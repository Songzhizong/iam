package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.annotations.ManualIdentityGenerator;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionGroup;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionItem;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionItemArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionItemInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
@Entity(name = PermissionItemDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = PermissionItemDO.TABLE_NAME,
        indexes = {
                @Index(name = "idx01_" + PermissionItemDO.TABLE_NAME, columnList = "app_id_"),
                @Index(name = "idx02_" + PermissionItemDO.TABLE_NAME, columnList = "group_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class PermissionItemDO implements PermissionItem {
    public static final String TABLE_NAME = "iam_permission_item";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @ManualIdentityGenerator(name = TABLE_NAME)
    private long id = -1L;

    @Comment("应用ID")
    @Column(nullable = false, name = "app_id_")
    private long appId = -1L;

    @Comment("权限组ID")
    @Column(nullable = false, name = "group_id_")
    private long groupId = -1L;

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
    public static PermissionItemDO create(long id, @Nonnull PermissionGroup group,
                                          @Nonnull CreatePermissionItemArgs args) {
        String name = args.getName();
        Asserts.notBlank(name, "权限项名称为空");
        Boolean enabled = args.getEnabled();
        if (enabled == null) {
            enabled = true;
        }
        PermissionItemDO permissionItemDO = new PermissionItemDO();
        permissionItemDO.setId(id);
        permissionItemDO.setAppId(group.getAppId());
        permissionItemDO.setGroupId(group.getId());
        permissionItemDO.setName(name);
        permissionItemDO.setEnabled(enabled);
        permissionItemDO.setOrderNum(args.getOrderNum());
        return permissionItemDO;
    }

    @Nonnull
    public static PermissionItemDO ofInfo(@Nonnull PermissionItemInfo info) {
        PermissionItemDO permissionItemDO = new PermissionItemDO();
        permissionItemDO.setId(info.getId());
        permissionItemDO.setAppId(info.getAppId());
        permissionItemDO.setGroupId(info.getGroupId());
        permissionItemDO.setName(info.getName());
        permissionItemDO.setEnabled(info.isEnabled());
        permissionItemDO.setOrderNum(info.getOrderNum());
        return permissionItemDO;
    }

    public void setOrderNum(@Nullable Integer orderNum) {
        if (orderNum == null) {
            orderNum = 0;
        }
        this.orderNum = orderNum;
    }
}
