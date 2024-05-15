package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.ManualIDGenerator;
import cn.sh.ideal.iam.infrastructure.encryption.EncryptionProvider;
import cn.sh.ideal.iam.infrastructure.encryption.EncryptionUtils;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/15
 */
@Getter
@Setter
@Entity(name = UserDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = UserDO.TABLE_NAME,
        indexes = {
                @Index(name = "uidx01_" + UserDO.TABLE_NAME, columnList = "tenant_id_,account_", unique = true),
                @Index(name = "idx01_" + UserDO.TABLE_NAME, columnList = "container_id_"),
                @Index(name = "idx02_" + UserDO.TABLE_NAME, columnList = "phone_"),
                @Index(name = "idx03_" + UserDO.TABLE_NAME, columnList = "email_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class UserDO implements User {
    public static final String TABLE_NAME = "iam_user";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(generator = TABLE_NAME)
    @GenericGenerator(name = TABLE_NAME, type = ManualIDGenerator.class)
    private Long id = -1L;

    @Comment("所属租户ID")
    @Column(nullable = false, name = "tenant_id_")
    private long tenantId = -1L;

    @Comment("安全容器ID")
    @Column(nullable = false, name = "container_id_")
    private long containerId;

    @Nonnull
    @Comment("用户姓名")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("账号")
    @Column(nullable = false, name = "account_")
    private String account = "";

    @Nonnull
    @Comment("手机号")
    @Column(nullable = false, name = "phone_")
    private String phone = "";

    @Nonnull
    @Comment("邮箱")
    @Column(nullable = false, name = "email_")
    private String email = "";

    @Nonnull
    @Comment("语言")
    @Column(nullable = false, name = "language_")
    private String language = "";

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @Nonnull
    public static UserDO create(long tenantId,
                                @Nonnull CreateUserArgs args,
                                @Nonnull OrganizationI18nReader i18nReader) {
        String name = args.getName();
        Asserts.notBlank(name, () -> i18nReader.getMessage("user.name.blank"));
        UserDO userDO = new UserDO();
        userDO.setTenantId(tenantId);
        userDO.setContainerId(args.getContainerId());
        userDO.setName(name);
        userDO.setAccount(args.getAccount());
        userDO.setPhone(args.getPhone());
        userDO.setEmail(args.getEmail());
        userDO.setLanguage(args.getLanguage());
        return userDO;
    }


    @Nullable
    @Override
    public Long getContainerId() {
        long containerId = this.containerId;
        if (containerId < 1) {
            return null;
        }
        return containerId;
    }

    public void setContainerId(@Nullable Long containerId) {
        if (containerId == null || containerId < 1) {
            containerId = -1L;
        }
        this.containerId = containerId;
    }

    @Nonnull
    @Override
    public String getAccount() {
        String account = this.account;
        if (EncryptionProvider.isEmpty(account)) {
            return "";
        }
        return account;
    }

    public void setAccount(@Nullable String account) {
        if (StringUtils.isBlank(account)) {
            account = EncryptionProvider.randomEmpty();
        }
        this.account = account;
    }

    @Nonnull
    @Override
    public String getPhone() {
        return EncryptionUtils.decrypt(phone);
    }

    public void setPhone(@Nullable String phone) {
        this.phone = EncryptionUtils.encrypt(phone);
    }

    @Nonnull
    @Override
    public String getEmail() {
        return EncryptionUtils.decrypt(email);
    }

    public void setEmail(@Nullable String email) {
        this.email = EncryptionUtils.encrypt(email);
    }

    public void setLanguage(@Nullable String language) {
        if (language == null) {
            language = "";
        }
        this.language = language;
    }
}
