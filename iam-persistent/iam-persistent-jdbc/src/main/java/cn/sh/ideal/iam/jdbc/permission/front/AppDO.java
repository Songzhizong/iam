package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.ManualIDGenerator;
import cn.sh.ideal.iam.core.constant.Terminal;
import cn.sh.ideal.iam.permission.core.PermissionModel;
import cn.sh.ideal.iam.permission.front.domain.model.App;
import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
@Entity(name = AppDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = AppDO.TABLE_NAME,
        indexes = {
                @Index(name = "uidx01_" + AppDO.TABLE_NAME, columnList = "terminal_,root_path_", unique = true),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class AppDO implements App {
    public static final String TABLE_NAME = "iam_front_app";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(generator = TABLE_NAME)
    @GenericGenerator(name = TABLE_NAME, type = ManualIDGenerator.class)
    private long id = -1L;

    @Nonnull
    @Comment("终端类型")
    @Enumerated(EnumType.STRING)
    @JdbcType(VarcharJdbcType.class)
    @Column(nullable = false, name = "terminal_")
    private Terminal terminal = Terminal.WEB;

    @Nonnull
    @Comment("权限模型")
    @Enumerated(EnumType.STRING)
    @JdbcType(VarcharJdbcType.class)
    @Column(nullable = false, name = "permission_model_")
    private PermissionModel permissionModel = PermissionModel.TBAC;

    @Nonnull
    @Comment("跟路径, 同终端下全局唯一")
    @Column(nullable = false, name = "root_path_")
    private String rootPath = "";

    @Nonnull
    @Comment("名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("备注")
    @Column(nullable = false, name = "note_")
    private String note = "";

    @Comment("排序值")
    @Column(nullable = false, name = "order_num_")
    private int orderNum = 0;

    @Nonnull
    @Comment("配置信息")
    @Column(nullable = false, name = "config_", length = 2000)
    private String config = "";

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @Nonnull
    public static AppDO create(long id, @Nonnull CreateAppArgs args) {
        Terminal terminal = args.getTerminal();
        String rootPath = args.getRootPath();
        String name = args.getName();
        Asserts.nonnull(terminal, "应用终端类型为空");
        Asserts.notBlank(rootPath, "应用根路径为空");
        Asserts.notBlank(name, "应用名称为空");
        AppDO appDO = new AppDO();
        appDO.setId(id);
        appDO.setTerminal(terminal);
        appDO.setRootPath(rootPath);
        appDO.setName(name);
        appDO.setNote(args.getNote());
        appDO.setOrderNum(args.getOrderNum());
        appDO.setConfig(args.getConfig());
        return appDO;
    }

    @Nonnull
    public static AppDO ofInfo(@Nonnull AppInfo info) {
        AppDO appDO = new AppDO();
        appDO.setId(info.getId());
        appDO.setTerminal(info.getTerminal());
        appDO.setRootPath(info.getRootPath());
        appDO.setName(info.getName());
        appDO.setNote(info.getNote());
        appDO.setOrderNum(info.getOrderNum());
        appDO.setConfig(info.getConfig());
        return appDO;
    }

    public void setNote(@Nullable String note) {
        if (note == null) {
            note = "";
        }
        this.note = note;
    }

    public void setConfig(@Nullable String config) {
        if (config == null) {
            config = "";
        }
        this.config = config;
    }

    public void setOrderNum(@Nullable Integer orderNum) {
        if (orderNum == null) {
            orderNum = 0;
        }
        this.orderNum = orderNum;
    }
}
