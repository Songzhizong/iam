package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.idealio.framework.util.data.hibernate.JpaIDGenerator;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssign;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class PermissionAssignDO implements PermissionAssign {
    public static final String TABLE_NAME = "iam_tbac_permission_assign";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(generator = TABLE_NAME)
    @GenericGenerator(name = TABLE_NAME, type = JpaIDGenerator.class)
    private Long id = null;

    @Comment("应用ID")
    @Column(nullable = false, name = "app_id_")
    private long appId;

    @Comment("安全容器ID")
    @Column(nullable = false, name = "container_id_")
    private long containerId;
}
