package cn.sh.ideal.iam.jdbc.permission.front;

import cn.idealio.framework.util.data.hibernate.ManualIDGenerator;
import cn.sh.ideal.iam.core.constant.Terminal;
import cn.sh.ideal.iam.permission.front.domain.model.App;
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
    private Long id = -1L;

    @Nonnull
    @Comment("终端类型")
    @Enumerated(EnumType.STRING)
    @JdbcType(VarcharJdbcType.class)
    @Column(nullable = false, name = "terminal_")
    private Terminal terminal = Terminal.WEB;

    @Nonnull
    @Comment("跟路径, 同终端下全局唯一")
    @Column(nullable = false, name = "root_path_")
    private String rootPath = "";

    @Nonnull
    @Comment("名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

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
    public static AppDO ofInfo(@Nonnull AppInfo info) {
        AppDO appDO = new AppDO();
        appDO.setId(info.getId());
        appDO.setTerminal(info.getTerminal());
        appDO.setRootPath(info.getRootPath());
        appDO.setName(info.getName());
        appDO.setOrderNum(info.getOrderNum());
        appDO.setConfig(info.getConfig());
        return appDO;
    }
}
