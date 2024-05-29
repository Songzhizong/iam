package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.Asserts;
import cn.idealio.framework.util.data.hibernate.annotations.JpaIdentityGenerator;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Platform;
import cn.sh.ideal.iam.organization.dto.args.CreatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.args.UpdatePlatformArgs;
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
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Getter
@Setter
@Entity(name = PlatformDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = PlatformDO.TABLE_NAME, indexes = {
        @Index(name = "uidx01_" + PlatformDO.TABLE_NAME, columnList = "code_", unique = true),
})
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class PlatformDO implements Platform {
    public static final String TABLE_NAME = "iam_platform";
    /** 平台编码正则, 字母开头, 仅支持小写字母、数字、下划线 */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*$");

    @Id
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @JpaIdentityGenerator(name = TABLE_NAME)
    private Long id = null;

    @Nonnull
    @Comment("平台编码")
    @Column(nullable = false, name = "code_")
    private String code = "";

    @Nonnull
    @Comment("名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Nonnull
    @Comment("对外显示名称")
    @Column(nullable = false, name = "open_name_")
    private String openName = "";

    @Nonnull
    @Comment("备注")
    @Column(nullable = false, name = "note_")
    private String note = "";

    @Comment("是否允许注册")
    @Column(nullable = false, name = "registrable_")
    private boolean registrable = false;

    @Nonnull
    @Comment("配置信息")
    @Column(nullable = false, name = "config_")
    private String config = "";

    @Comment("是否已被删除")
    @Column(nullable = false, name = "deleted_")
    private boolean deleted = false;

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @Nonnull
    public static PlatformDO create(@Nonnull CreatePlatformArgs args,
                                    @Nonnull IamI18nReader i18nReader) {
        String code = args.getCode();
        String name = args.getName();
        Asserts.notBlank(code, () -> i18nReader.getMessage("platform.code.required"));
        Asserts.notBlank(name, () -> i18nReader.getMessage("platform.name.required"));
        if (!CODE_PATTERN.matcher(code).matches()) {
            log.info("创建平台失败, 编码不合法: {}", code);
            throw new BadRequestException(i18nReader.getMessage("platform.code.invalid"));
        }
        String openName = args.getOpenName();
        if (StringUtils.isBlank(openName)) {
            openName = name;
        }
        PlatformDO platformDO = new PlatformDO();
        platformDO.setCode(code);
        platformDO.setName(name);
        platformDO.setOpenName(openName);
        platformDO.setNote(args.getNote());
        platformDO.setRegistrable(args.getRegistrable());
        platformDO.setConfig(args.getConfig());
        return platformDO;
    }

    @Override
    public void delete() {
        Long id = getId();
        String code = getCode();
        code = code + "_" + id;
        setCode(code);
        setDeleted(true);
    }

    @Override
    public void update(@Nonnull UpdatePlatformArgs args,
                       @Nonnull IamI18nReader i18nReader) {
        String name = args.getName();
        String openName = args.getOpenName();
        Asserts.notBlank(name, () -> i18nReader.getMessage("platform.name.required"));
        if (StringUtils.isBlank(openName)) {
            openName = name;
        }
        this.setName(name);
        this.setOpenName(openName);
        this.setNote(args.getNote());
        this.setRegistrable(args.getRegistrable());
        this.setConfig(args.getConfig());
    }

    public void setNote(@Nullable String note) {
        if (note == null) {
            note = "";
        }
        this.note = note;
    }

    public void setRegistrable(@Nullable Boolean registrable) {
        if (registrable == null) {
            registrable = false;
        }
        this.registrable = registrable;
    }

    public void setConfig(@Nullable String config) {
        if (config == null) {
            config = "";
        }
        this.config = config;
    }
}
