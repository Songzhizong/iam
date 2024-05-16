package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.*;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateSecurityContainerArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Component("organizationEntityFactory")
public class EntityFactoryImpl implements EntityFactory {
    @Nonnull
    @Override
    public Tenant tenant(@Nonnull CreateTenantArgs args,
                         @Nonnull OrganizationI18nReader i18nReader) {
        return TenantDO.create(args, i18nReader);
    }

    @Nonnull
    @Override
    public SecurityContainer securityContainer(@Nullable SecurityContainer parent,
                                               @Nonnull CreateSecurityContainerArgs args,
                                               @Nonnull OrganizationI18nReader i18nReader) {
        return SecurityContainerDO.create(parent, args, i18nReader);
    }

    @Nonnull
    @Override
    public Group group(long tenantId,
                       @Nonnull CreateGroupArgs args,
                       @Nonnull OrganizationI18nReader i18nReader) {
        return GroupDO.create(tenantId, args, i18nReader);
    }

    @Nonnull
    @Override
    public User user(long tenantId, @Nonnull CreateUserArgs args, @Nonnull OrganizationI18nReader i18nReader) {
        return UserDO.create(tenantId, args, i18nReader);
    }

}
