package cn.sh.ideal.iam.organization.domain.model;

import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import cn.sh.ideal.iam.organization.dto.args.CreatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface EntityFactory {

    @Nonnull
    Platform platform(@Nonnull CreatePlatformArgs args,
                      @Nonnull OrganizationI18nReader i18nReader);

    @Nonnull
    Tenant tenant(long id,
                  @Nonnull Platform platform,
                  @Nonnull CreateTenantArgs args,
                  @Nonnull OrganizationI18nReader i18nReader);

    @Nonnull
    UserGroup group(@Nonnull Tenant tenant,
                    @Nonnull CreateGroupArgs args,
                    @Nonnull OrganizationI18nReader i18nReader);

    @Nonnull
    User user(@Nonnull Tenant tenant,
              @Nonnull CreateUserArgs args,
              @Nonnull OrganizationI18nReader i18nReader);
}
