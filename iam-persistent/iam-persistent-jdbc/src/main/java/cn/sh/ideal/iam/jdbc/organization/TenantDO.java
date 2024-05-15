package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.ManualIDGenerator;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Getter
@Setter
@Entity(name = TenantDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = TenantDO.TABLE_NAME,
        indexes = {
                @Index(name = "uidx01_" + TenantDO.TABLE_NAME, columnList = "abbreviation_", unique = true),
                @Index(name = "idx01_" + TenantDO.TABLE_NAME, columnList = "container_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class TenantDO implements Tenant {
    public static final String TABLE_NAME = "iam_tenant";

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(generator = TABLE_NAME)
    @GenericGenerator(name = TABLE_NAME, type = ManualIDGenerator.class)
    private Long id = -1L;

    @Comment("安全容器ID")
    @Column(nullable = false, name = "container_id_")
    private long containerId = -1L;

    @Nonnull
    @Comment("租户名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("租户缩写")
    @Column(nullable = false, name = "abbreviation_")
    private String abbreviation = "";

    @Nonnull
    @Comment("描述信息")
    @Column(nullable = false, name = "note_")
    private String note = "";

    @Nonnull
    @Comment("系统版本编码")
    @Column(nullable = false, name = "system_edition_")
    private String systemEdition = "";

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @Nonnull
    public static TenantDO create(@Nonnull CreateTenantArgs args,
                                  @Nonnull OrganizationI18nReader i18nReader) {
        String name = args.getName();
        String abbreviation = args.getAbbreviation();
        Asserts.notBlank(name, () -> i18nReader.getMessage("tenant.name.blank"));
        Asserts.notBlank(abbreviation, () -> i18nReader.getMessage("tenant.abbreviation.blank"));
        TenantDO tenantDO = new TenantDO();
        tenantDO.setContainerId(args.getContainerId());
        tenantDO.setName(name);
        tenantDO.setAbbreviation(abbreviation);
        tenantDO.setNote(args.getNote());
        tenantDO.setSystemEdition(args.getSystemEdition());
        return tenantDO;
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
        if (StringUtils.isBlank(note)) {
            note = "";
        }
        this.note = note;
    }

    public void setSystemEdition(@Nullable String systemEdition) {
        if (StringUtils.isBlank(systemEdition)) {
            systemEdition = "";
        }
        this.systemEdition = systemEdition;
    }
}
