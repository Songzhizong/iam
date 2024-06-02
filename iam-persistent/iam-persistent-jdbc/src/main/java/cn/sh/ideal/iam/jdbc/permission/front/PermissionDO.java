package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.annotations.ManualIdentityGenerator;
import cn.idealio.framework.util.data.jpa.LongSetBinaryConverter;
import cn.idealio.framework.util.data.jpa.StringSetGzipConverter;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionItem;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.LongVarbinaryJdbcType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/30
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
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class PermissionDO implements Permission {
    public static final String TABLE_NAME = "iam_permission";

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
    @Comment("权限组ID")
    @Column(nullable = false, name = "group_id_")
    private Long groupId = -1L;

    @Nonnull
    @Comment("权限项ID")
    @Column(nullable = false, name = "item_id_")
    private Long itemId = -1L;

    @Nonnull
    @Comment("名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("唯一标识符, 应用下唯一")
    @Column(nullable = false, name = "ident_")
    private String ident = "";

    @Nonnull
    @Comment("api pattern清单")
    @JdbcType(LongVarbinaryJdbcType.class)
    @Convert(converter = StringSetGzipConverter.class)
    @Column(nullable = false, name = "api_patterns_")
    private Set<String> apiPatterns = new LinkedHashSet<>();

    @Nonnull
    @Comment("明确的api接口地址清单")
    @JdbcType(LongVarbinaryJdbcType.class)
    @Convert(converter = StringSetGzipConverter.class)
    @Column(nullable = false, name = "specific_apis_")
    private Set<String> specificApis = new LinkedHashSet<>();

    @Nonnull
    @Comment("权限标识列表")
    @JdbcType(LongVarbinaryJdbcType.class)
    @Convert(converter = StringSetGzipConverter.class)
    @Column(nullable = false, name = "authorities_")
    private Set<String> authorities = new LinkedHashSet<>();

    @Nonnull
    @Comment("子权限ID列表")
    @JdbcType(LongVarbinaryJdbcType.class)
    @Convert(converter = LongSetBinaryConverter.class)
    @Column(nullable = false, name = "child_ids_")
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

    @Nonnull
    public static PermissionDO create(@Nonnull Long id, @Nonnull PermissionItem item,
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
        permissionDO.setIdent(args.getIdent());
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
        permissionDO.setIdent(info.getIdent());
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

    public void setIdent(@Nullable String ident) {
        if (StringUtils.isBlank(ident)) {
            ident = "";
        }
        this.ident = ident;
    }

    public void setOrderNum(@Nullable Integer orderNum) {
        if (orderNum == null) {
            orderNum = 0;
        }
        this.orderNum = orderNum;
    }
}
