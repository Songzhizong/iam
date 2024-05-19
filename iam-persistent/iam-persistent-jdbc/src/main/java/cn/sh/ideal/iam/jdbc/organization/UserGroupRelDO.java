package cn.sh.ideal.iam.jdbc.organization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/15
 */
@Getter
@Setter
@Entity(name = UserGroupRelDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = UserGroupRelDO.TABLE_NAME,
        indexes = {
                @Index(name = "idx01_" + UserGroupRelDO.TABLE_NAME, columnList = "user_id_"),
                @Index(name = "idx02_" + UserGroupRelDO.TABLE_NAME, columnList = "group_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class UserGroupRelDO {
    public static final String TABLE_NAME = "iam_user_group_user_rel";

    @Id
    @Nullable
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null;

    @Comment("用户ID")
    @Column(nullable = false, name = "user_id_")
    private long userId = -1L;

    @Comment("用户组ID")
    @Column(nullable = false, name = "group_id_")
    private long groupId = -1L;

    @Nonnull
    public static UserGroupRelDO create(long userId, long groupId) {
        UserGroupRelDO userGroupRelDO = new UserGroupRelDO();
        userGroupRelDO.setUserId(userId);
        userGroupRelDO.setGroupId(groupId);
        return userGroupRelDO;

    }
}