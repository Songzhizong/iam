package cn.sh.ideal.iam.jdbc.factor.otp;

import cn.idealio.framework.util.data.hibernate.annotations.ManualIdentityGenerator;
import cn.sh.ideal.iam.factor.otp.domain.model.UserTotp;
import cn.sh.ideal.iam.infrastructure.encryption.EncryptionUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Getter
@Setter
@Entity(name = UserTotpDO.TABLE_NAME)
@Table(name = UserTotpDO.TABLE_NAME)
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class UserTotpDO implements UserTotp {
    public static final String TABLE_NAME = "iam_user_totp";

    @Id
    @Nonnull
    @Comment("用户ID")
    @Column(nullable = false, name = "user_id_")
    @ManualIdentityGenerator(name = TABLE_NAME)
    private Long userId = -1L;

    @Nonnull
    @Comment("totp密钥")
    @Column(nullable = false, name = "secret_")
    private String secret = "";

    @Comment("创建时间")
    @Column(nullable = false, name = "created_time_")
    private long createdTime = 0;

    @Nonnull
    public String getSecret() {
        return EncryptionUtils.decrypt(secret);
    }

    public void setSecret(@Nonnull String secret) {
        this.secret = EncryptionUtils.encrypt(secret);
    }

    @Nonnull
    public static UserTotpDO create(@Nonnull Long userId, @Nonnull String secret) {
        UserTotpDO userTotpDO = new UserTotpDO();
        userTotpDO.setUserId(userId);
        userTotpDO.setSecret(secret);
        userTotpDO.setCreatedTime(System.currentTimeMillis());
        return userTotpDO;
    }
}
