package cn.sh.ideal.iam.jdbc.authorization.standard;

import cn.idealio.framework.util.data.hibernate.annotations.JpaIdentityGenerator;
import cn.sh.ideal.iam.authorization.standard.domain.model.AccessToken;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Getter
@Setter
@Entity(name = AccessTokenDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = AccessTokenDO.TABLE_NAME, indexes = {
        @Index(name = "idx01_" + AccessTokenDO.TABLE_NAME, columnList = "user_id_,client_id_"),
        @Index(name = "idx02_" + AccessTokenDO.TABLE_NAME, columnList = "expiration_"),
})
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class AccessTokenDO implements AccessToken {
    public static final String TABLE_NAME = "iam_access_token";

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
    @Comment("用户ID")
    @Column(nullable = false, name = "user_id_")
    private Long userId = -1L;

    @Nonnull
    @Comment("用户所属租户ID")
    @Column(nullable = false, name = "tenant_id_")
    private Long tenantId = -1L;

    @Nonnull
    @Comment("授权端ID")
    @Column(nullable = false, name = "client_id_")
    private Long clientId = -1L;

    @Comment("有效时长, 单位毫秒")
    @Column(nullable = false, name = "session_timeout_")
    private long sessionTimeout = 0;

    @Comment("过期时间戳")
    @Column(nullable = false, name = "expiration_")
    private long expiration = 0;

    @Comment("最近访问时间")
    @Column(nullable = false, name = "latest_activity_")
    private long latestActivity = 0;

    @Comment("创建时间")
    @Column(nullable = false, name = "created_time_")
    private long createdTime = 0;

    @Nonnull
    public static AccessTokenDO create(@Nonnull AuthClient authClient,
                                       @Nonnull UserDetail userDetail,
                                       long sessionTimeout) {
        long currentTimeMillis = System.currentTimeMillis();
        AccessTokenDO accessTokenDO = new AccessTokenDO();
        accessTokenDO.setPlatform(userDetail.getPlatform());
        accessTokenDO.setUserId(userDetail.getId());
        accessTokenDO.setTenantId(userDetail.getTenantId());
        accessTokenDO.setClientId(authClient.getId());
        accessTokenDO.setSessionTimeout(sessionTimeout);
        accessTokenDO.setExpiration(currentTimeMillis + sessionTimeout);
        accessTokenDO.setLatestActivity(currentTimeMillis);
        accessTokenDO.setCreatedTime(currentTimeMillis);
        return accessTokenDO;
    }
}
