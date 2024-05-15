package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.ManualIDGenerator;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Group;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
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
@Entity(name = GroupDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = GroupDO.TABLE_NAME,
        indexes = {
                @Index(name = "idx01_" + GroupDO.TABLE_NAME, columnList = "tenant_id_"),
                @Index(name = "idx02_" + GroupDO.TABLE_NAME, columnList = "container_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class GroupDO implements Group {
    public static final String TABLE_NAME = "iam_user_group";

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
    @Comment("用户组名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("备注信息")
    @Column(nullable = false, name = "note_")
    private String note = "";

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @Nonnull
    public static GroupDO create(long tenantId,
                                 @Nonnull CreateGroupArgs args,
                                 @Nonnull OrganizationI18nReader i18nReader) {
        Long containerId = args.getContainerId();
        String name = args.getName();
        String note = args.getNote();
        Asserts.notBlank(name, () -> i18nReader.getMessage("user_group.name.blank"));
        GroupDO groupDO = new GroupDO();
        groupDO.setTenantId(tenantId);
        groupDO.setContainerId(containerId);
        groupDO.setName(name);
        groupDO.setNote(note);
        return groupDO;
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

    public void setNote(@Nullable String note) {
        if (note == null) {
            note = "";
        }
        this.note = note;
    }
}
