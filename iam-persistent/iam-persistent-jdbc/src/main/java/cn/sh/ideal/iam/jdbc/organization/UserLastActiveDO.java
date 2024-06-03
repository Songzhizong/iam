package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.util.data.hibernate.annotations.ManualIdentityGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
@Entity(name = UserLastActiveDO.TABLE_NAME)
@Table(name = UserLastActiveDO.TABLE_NAME)
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class UserLastActiveDO {
    public static final String TABLE_NAME = "iam_user_last_active";

    @Id
    @Comment("用户ID")
    @Column(nullable = false, name = "id_")
    @ManualIdentityGenerator(name = TABLE_NAME)
    private Long id = null;


    @Comment("最近活跃时间")
    @Column(nullable = false, name = "last_active_time_")
    private long lastActiveTime = 0;
}
