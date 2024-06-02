package cn.sh.ideal.iam.jdbc.permission.tbac;

import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.permission.tbac.domain.model.SecurityContainer;
import cn.sh.ideal.iam.permission.tbac.dto.args.CreateSecurityContainerArgs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Getter
@Setter
@Entity(name = SecurityContainerDO.TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
@Table(name = SecurityContainerDO.TABLE_NAME,
        indexes = {
                @Index(name = "idx01_" + SecurityContainerDO.TABLE_NAME, columnList = "parent_id_"),
        })
@SuppressWarnings({"JpaDataSourceORMInspection", "RedundantSuppression", "NullableProblems"})
public class SecurityContainerDO implements SecurityContainer {
    public static final String TABLE_NAME = "iam_security_container";

    @Id
    @Nullable
    @Comment("主键")
    @Column(nullable = false, name = "id_")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id = null;

    @Nonnull
    @Comment("父ID")
    @Column(nullable = false, name = "parent_id_")
    private Long parentId = -1L;

    @Nonnull
    @Comment("路由键")
    @Column(nullable = false, name = "parent_route_")
    private String parentRoute = "";

    @Nonnull
    @Comment("容器名称")
    @Column(nullable = false, name = "name_")
    private String name = "";

    @Version
    @Column(nullable = false, name = "version_")
    private long version = 0;

    @CreatedDate
    @Column(nullable = false, name = "created_time_")
    private long createdTime = 0;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_time_")
    private long updatedTime = 0;

    @Nonnull
    public static SecurityContainerDO create(@Nullable SecurityContainer parent,
                                             @Nonnull CreateSecurityContainerArgs args,
                                             @Nonnull IamI18nReader i18nReader) {
        String name = args.getName();
        Asserts.notBlank(name, () -> i18nReader.getMessage("sc.name.blank"));
        SecurityContainerDO securityContainerDO = new SecurityContainerDO();
        securityContainerDO.setName(name);
        securityContainerDO.changeParent(parent, i18nReader);
        return securityContainerDO;
    }

    @Nullable
    public Long getParentId() {
        Long parentId = this.parentId;
        if (parentId < 0) {
            return null;
        }
        return parentId;
    }

    public void setParentId(@Nullable Long parentId) {
        if (parentId == null || parentId < 1) {
            parentId = -1L;
        }
        this.parentId = parentId;
    }
}
