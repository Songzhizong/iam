package cn.sh.ideal.iam.jdbc.authorization.standard;

import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.annotations.JpaIdentityGenerator;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import cn.sh.ideal.iam.core.constant.Terminal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Getter
@Setter
@Entity(name = AuthClientDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = AuthClientDO.TABLE_NAME, indexes = {
        @Index(name = "idx01_" + AuthClientDO.TABLE_NAME, columnList = "token_"),
})
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class AuthClientDO implements AuthClient {
    public static final String TABLE_NAME = "iam_auth_client";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @JpaIdentityGenerator(name = TABLE_NAME)
    private Long id = null;

    @Nonnull
    @Comment("所属平台")
    @Column(nullable = false, name = "platform_")
    private String platform = "";

    @Nonnull
    @Comment("名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("备注")
    @Column(nullable = false, name = "note_")
    private String note = "";

    @Nonnull
    @Comment("终端类型")
    @Enumerated(EnumType.STRING)
    @JdbcType(VarcharJdbcType.class)
    @Column(nullable = false, name = "terminal_")
    private Terminal terminal = Terminal.WEB;

    @Nonnull
    @Comment("token")
    @Column(nullable = false, name = "token_")
    private String token = "";

    @Version
    @Comment("乐观锁版本")
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @CreatedDate
    @Comment("创建时间")
    @Column(nullable = false, name = "created_time_")
    private long createdTime = 0;

    @LastModifiedDate
    @Comment("更新时间")
    @Column(nullable = false, name = "updated_time_")
    private long updatedTime = 0;

    @Nonnull
    public static AuthClientDO create(@Nonnull CreateAuthClientArgs args) {
        String platform = args.getPlatform();
        String name = args.getName();
        Terminal terminal = args.getTerminal();
        Asserts.notBlank(platform, "平台编码为空");
        Asserts.notBlank(name, "名称为空");
        Asserts.nonnull(terminal, "终端类型为空");
        AuthClientDO authClientDO = new AuthClientDO();
        authClientDO.setPlatform(platform);
        authClientDO.setName(name);
        authClientDO.setNote(args.getNote());
        authClientDO.setTerminal(terminal);
        authClientDO.setToken(UUID.randomUUID().toString());
        return authClientDO;
    }

    public void setNote(@Nullable String note) {
        if (note == null) {
            note = "";
        }
        this.note = note;
    }
}
