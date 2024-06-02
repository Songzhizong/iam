package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.NumberSystemConverter;
import cn.idealio.framework.util.data.hibernate.annotations.ManualIdentityGenerator;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Platform;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Getter
@Setter
@Entity(name = TenantDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = TenantDO.TABLE_NAME,
        indexes = {
                @Index(name = "uidx01_" + TenantDO.TABLE_NAME, columnList = "platform_,abbreviation_", unique = true),
                @Index(name = "idx01_" + TenantDO.TABLE_NAME, columnList = "container_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class TenantDO implements Tenant {
    public static final String TABLE_NAME = "iam_tenant";
    // 租户缩写正则表达式, 仅支持大小字母/数字/下划线
    private static final Pattern ABBREVIATION_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Id
    @Nonnull
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @ManualIdentityGenerator(name = TABLE_NAME)
    private Long id = -1L;

    @Nonnull
    @Comment("所属平台")
    @Column(nullable = false, name = "platform_")
    private String platform = "";

    @Nonnull
    @Comment("安全容器ID")
    @Column(nullable = false, name = "container_id_")
    private Long containerId = -1L;

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

    @Comment("租户是否被锁定")
    @Column(nullable = false, name = "blocked_")
    private boolean blocked = false;

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @Nonnull
    public static TenantDO create(@Nonnull Long id,
                                  @Nonnull Platform platform,
                                  @Nonnull CreateTenantArgs args,
                                  @Nonnull IamI18nReader i18nReader) {
        String name = args.getName();
        String abbreviation = args.getAbbreviation();
        Asserts.notBlank(name, () -> i18nReader.getMessage("tenant.name.blank"));
        Asserts.notBlank(abbreviation, () -> i18nReader.getMessage("tenant.abbreviation.blank"));
        if (!ABBREVIATION_PATTERN.matcher(abbreviation).matches()) {
            log.info("租户缩写不合法: {}", abbreviation);
            throw new BadRequestException(i18nReader.getMessage("tenant.abbreviation.invalid"));
        }
        TenantDO tenantDO = new TenantDO();
        tenantDO.setId(id);
        tenantDO.setPlatform(platform.getCode());
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

    public static void main(String[] args) {
        long currentTimeMillis = Long.MAX_VALUE;
        System.out.println(NumberSystemConverter.to58(currentTimeMillis));
    }
}
