package cn.sh.ideal.iam.jdbc.authorization.standard;

import cn.sh.ideal.iam.authorization.standard.domain.model.AccessToken;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.domain.model.EntityFactory;
import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Component("authorizationStandardEntityFactory")
public class EntityFactoryImpl implements EntityFactory {

    @Nonnull
    @Override
    public AuthClient authClient(@Nonnull CreateAuthClientArgs args) {
        return AuthClientDO.create(args);
    }

    @Nonnull
    @Override
    public AccessToken accessToken(@Nonnull AuthClient authClient,
                                   @Nonnull UserDetail userDetail,
                                   long sessionTimeout) {
        return AccessTokenDO.create(authClient, userDetail, sessionTimeout);
    }

}
