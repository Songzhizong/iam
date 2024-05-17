package cn.sh.ideal.iam.organization.domain.model;

import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateSecurityContainerArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface EntityFactory {

    @Nonnull
    Tenant tenant(long id, @Nonnull CreateTenantArgs args,
                  @Nonnull OrganizationI18nReader i18nReader);

    @Nonnull
    SecurityContainer securityContainer(@Nullable SecurityContainer parent,
                                        @Nonnull CreateSecurityContainerArgs args,
                                        @Nonnull OrganizationI18nReader i18nReader);

    @Nonnull
    Group group(long tenantId,
                @Nonnull CreateGroupArgs args,
                @Nonnull OrganizationI18nReader i18nReader);

    @Nonnull
    User user(long tenantId,
              @Nonnull CreateUserArgs args,
              @Nonnull OrganizationI18nReader i18nReader);
}
