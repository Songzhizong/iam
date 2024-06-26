package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.annotations.JpaIdentityGenerator;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.infrastructure.encoder.password.PasswordEncoder;
import cn.sh.ideal.iam.infrastructure.encryption.EncryptionProvider;
import cn.sh.ideal.iam.infrastructure.encryption.EncryptionUtils;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * @author 宋志宗 on 2024/5/15
 */
@Slf4j
@Getter
@Setter
@Entity(name = UserDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = UserDO.TABLE_NAME, indexes = {
        @Index(name = "uidx01_" + UserDO.TABLE_NAME, columnList = "tenant_id_,account_", unique = true),
        @Index(name = "uidx02_" + UserDO.TABLE_NAME, columnList = "email_,platform_", unique = true),
        @Index(name = "idx01_" + UserDO.TABLE_NAME, columnList = "container_id_"),
        @Index(name = "idx02_" + UserDO.TABLE_NAME, columnList = "phone_"),
})
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class UserDO implements User {
    public static final String TABLE_NAME = "iam_user";
    /** 账号正则表达式, 大小写字母开头且仅支持大小写字母/数字/下划线 */
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]+$");

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
    @Comment("所属租户ID")
    @Column(nullable = false, name = "tenant_id_")
    private Long tenantId = -1L;

    @Nonnull
    @Comment("安全容器ID")
    @Column(nullable = false, name = "container_id_")
    private Long containerId = -1L;

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

    @Nonnull
    @Comment("密码")
    @Column(nullable = false, name = "password_")
    private String password = "";

    @Comment("密码修改时间")
    @Column(nullable = false, name = "password_time_")
    private long passwordTime = 0;

    @Comment("账号是否被锁定")
    @Column(nullable = false, name = "blocked_")
    private boolean blocked = false;

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
    public static UserDO create(@Nonnull Tenant tenant,
                                @Nonnull CreateUserArgs args,
                                @Nonnull IamI18nReader i18nReader,
                                @Nonnull PasswordEncoder passwordEncoder) {
        String name = args.getName();
        String account = args.getAccount();
        String password = args.getPassword();
        Asserts.notBlank(name, () -> i18nReader.getMessage("user.name.blank"));
        if (StringUtils.isNotBlank(account) && !ACCOUNT_PATTERN.matcher(account).matches()) {
            log.info("用户账号不合法: {}", account);
            throw new BadRequestException(i18nReader.getMessage("user.account.invalid"));
        }
        UserDO userDO = new UserDO();
        userDO.setPlatform(tenant.getPlatform());
        userDO.setTenantId(tenant.getId());
        userDO.setContainerId(args.getContainerId());
        userDO.setName(name);
        userDO.setAccount(account);
        userDO.setPhone(args.getPhone());
        userDO.setEmail(args.getEmail());
        userDO.setLanguage(args.getLanguage());
        if (StringUtils.isNotBlank(password)) {
            String encode = passwordEncoder.encode(password);
            userDO.setPassword(encode);
            userDO.setPasswordTime(System.currentTimeMillis());
        }
        return userDO;
    }


    @Nullable
    @Override
    public Long getContainerId() {
        Long containerId = this.containerId;
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
