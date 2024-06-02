package cn.sh.ideal.iam.organization.domain.model;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.infrastructure.encoder.password.PasswordEncoder;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import cn.sh.ideal.iam.organization.dto.args.CreatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import cn.sh.ideal.iam.organization.dto.resp.PlatformInfo;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface OrganizationEntityFactory {

    @Nonnull
    Platform platform(@Nonnull Long id,
                      @Nonnull CreatePlatformArgs args,
                      @Nonnull IamI18nReader i18nReader);

    @Nonnull
    Platform platform(@Nonnull PlatformInfo platformInfo);

    @Nonnull
    Tenant tenant(@Nonnull Long id,
                  @Nonnull Platform platform,
                  @Nonnull CreateTenantArgs args,
                  @Nonnull IamI18nReader i18nReader);

    @Nonnull
    UserGroup group(@Nonnull Tenant tenant,
                    @Nonnull CreateGroupArgs args,
                    @Nonnull IamI18nReader i18nReader);

    @Nonnull
    User user(@Nonnull Tenant tenant,
              @Nonnull CreateUserArgs args,
              @Nonnull IamI18nReader i18nReader,
              @Nonnull PasswordEncoder passwordEncoder);
}
