package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.ManualIDGenerator;
import cn.idealio.framework.util.data.jpa.LongSetConverter;
import cn.idealio.framework.util.data.jpa.StringSetConverter;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionItem;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
@Entity(name = PermissionDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = PermissionDO.TABLE_NAME,
        indexes = {
                @Index(name = "idx01_" + PermissionDO.TABLE_NAME, columnList = "app_id_"),
                @Index(name = "idx02_" + PermissionDO.TABLE_NAME, columnList = "item_id_"),
                @Index(name = "idx03_" + PermissionDO.TABLE_NAME, columnList = "group_id_"),
                @Index(name = "idx04_" + PermissionDO.TABLE_NAME, columnList = "updated_time_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class PermissionDO implements Permission {
    public static final String TABLE_NAME = "iam_permission";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(generator = TABLE_NAME)
    @GenericGenerator(name = TABLE_NAME, type = ManualIDGenerator.class)
    private Long id = -1L;

    @Comment("应用ID")
    @Column(nullable = false, name = "app_id_")
    private long appId = -1L;

    @Comment("权限组ID")
    @Column(nullable = false, name = "group_id_")
    private long groupId = -1L;

    @Comment("权限项ID")
    @Column(nullable = false, name = "item_id_")
    private long itemId = -1L;

    @Nonnull
    @Comment("名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("api pattern清单")
    @Convert(converter = StringSetConverter.class)
    @Column(nullable = false, length = 2000, name = "api_patterns_")
    private Set<String> apiPatterns = new LinkedHashSet<>();

    @Nonnull
    @Comment("明确的api接口地址清单")
    @Convert(converter = StringSetConverter.class)
    @Column(nullable = false, length = 2000, name = "specific_apis_")
    private Set<String> specificApis = new LinkedHashSet<>();

    @Nonnull
    @Comment("权限标识列表")
    @Convert(converter = StringSetConverter.class)
    @Column(nullable = false, length = 2000, name = "authorities_")
    private Set<String> authorities = new LinkedHashSet<>();

    @Nonnull
    @Comment("子权限ID列表")
    @Convert(converter = LongSetConverter.class)
    @Column(nullable = false, length = 2000, name = "child_ids_")
    private Set<Long> childIds = new LinkedHashSet<>();

    @Comment("是否拥有所在配置项安全权限")
    @Column(nullable = false, name = "item_security_")
    private boolean itemSecurity = false;

    @Comment("是否拥有所在配置组安全权限")
    @Column(nullable = false, name = "group_security_")
    private boolean groupSecurity = false;

    @Comment("是否拥有配置项内所有权限")
    @Column(nullable = false, name = "all_in_item_")
    private boolean allInItem = false;

    @Comment("是否启用")
    @Column(nullable = false, name = "enabled_")
    private boolean enabled = true;

    @Comment("排序值")
    @Column(nullable = false, name = "order_num_")
    private int orderNum = 0;

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @CreatedDate
    @Column(nullable = false, name = "created_time_")
    private long createdTime = 0;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_time_")
    private long updatedTime = 0;

    @Nonnull
    public static PermissionDO create(long id, @Nonnull PermissionItem item,
                                      @Nonnull CreatePermissionArgs args) {
        String name = args.getName();
        Set<String> apis = args.getApis();
        Set<String> authorities = args.getAuthorities();
        Set<Long> childIds = args.getChildIds();
        Boolean itemSecurity = args.getItemSecurity();
        Boolean groupSecurity = args.getGroupSecurity();
        Boolean allInItem = args.getAllInItem();
        Boolean enabled = args.getEnabled();
        Asserts.notBlank(name, "权限名称为空");
        if (apis == null) {
            apis = new LinkedHashSet<>();
        }
        if (authorities == null) {
            authorities = new LinkedHashSet<>();
        }
        if (childIds == null) {
            childIds = new LinkedHashSet<>();
        }
        if (itemSecurity == null) {
            itemSecurity = false;
        }
        if (groupSecurity == null) {
            groupSecurity = false;
        }
        if (allInItem == null) {
            allInItem = false;
        }
        if (enabled == null) {
            enabled = true;
        }
        PermissionDO permissionDO = new PermissionDO();
        permissionDO.setId(id);
        permissionDO.setAppId(item.getAppId());
        permissionDO.setGroupId(item.getGroupId());
        permissionDO.setItemId(item.getId());
        permissionDO.setName(name);
        permissionDO.setApis(apis);
        permissionDO.setAuthorities(authorities);
        permissionDO.setChildIds(childIds);
        permissionDO.setItemSecurity(itemSecurity);
        permissionDO.setGroupSecurity(groupSecurity);
        permissionDO.setAllInItem(allInItem);
        permissionDO.setEnabled(enabled);
        permissionDO.setOrderNum(args.getOrderNum());
        return permissionDO;
    }

    @Nonnull
    public static PermissionDO ofInfo(@Nonnull PermissionInfo info) {
        PermissionDO permissionDO = new PermissionDO();
        permissionDO.setId(info.getId());
        permissionDO.setAppId(info.getAppId());
        permissionDO.setGroupId(info.getGroupId());
        permissionDO.setItemId(info.getItemId());
        permissionDO.setName(info.getName());
        permissionDO.setApis(info.getApis());
        permissionDO.setAuthorities(info.getAuthorities());
        permissionDO.setChildIds(info.getChildIds());
        permissionDO.setItemSecurity(info.isItemSecurity());
        permissionDO.setGroupSecurity(info.isGroupSecurity());
        permissionDO.setAllInItem(info.isAllInItem());
        permissionDO.setEnabled(info.isEnabled());
        permissionDO.setOrderNum(info.getOrderNum());
        return permissionDO;
    }

    public void setOrderNum(@Nullable Integer orderNum) {
        if (orderNum == null) {
            orderNum = 0;
        }
        this.orderNum = orderNum;
    }
}