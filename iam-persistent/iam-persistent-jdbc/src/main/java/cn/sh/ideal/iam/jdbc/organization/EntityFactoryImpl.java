package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.infrastructure.encoder.password.PasswordEncoder;
import cn.sh.ideal.iam.organization.domain.model.*;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import cn.sh.ideal.iam.organization.dto.args.CreatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Component("organizationEntityFactory")
public class EntityFactoryImpl implements EntityFactory {
    @Nonnull
    @Override
    public Platform platform(@Nonnull CreatePlatformArgs args,
                             @Nonnull IamI18nReader i18nReader) {
        return PlatformDO.create(args, i18nReader);
    }

    @Nonnull
    @Override
    public Tenant tenant(long id,
                         @Nonnull Platform platform,
                         @Nonnull CreateTenantArgs args,
                         @Nonnull IamI18nReader i18nReader) {
        return TenantDO.create(id, platform, args, i18nReader);
    }

    @Nonnull
    @Override
    public UserGroup group(@Nonnull Tenant tenant,
                           @Nonnull CreateGroupArgs args,
                           @Nonnull IamI18nReader i18nReader) {
        return UserGroupDO.create(tenant, args, i18nReader);
    }

    @Nonnull
    @Override
    public User user(@Nonnull Tenant tenant,
                     @Nonnull CreateUserArgs args,
                     @Nonnull IamI18nReader i18nReader,
                     @Nonnull PasswordEncoder passwordEncoder) {
        return UserDO.create(tenant, args, i18nReader, passwordEncoder);
    }

}
