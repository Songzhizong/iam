package cn.sh.ideal.iam.jdbc.authorization.standard;

import cn.sh.ideal.iam.authorization.standard.domain.model.AccessToken;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientInfo;
import cn.sh.ideal.iam.authorization.standard.domain.model.StandardAuthorizationEntityFactory;
import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Component
public class StandardAuthorizationEntityFactoryImpl implements StandardAuthorizationEntityFactory {

    @Nonnull
    @Override
    public AuthClient authClient(@Nonnull Long id, @Nonnull CreateAuthClientArgs args) {
        return AuthClientDO.create(id, args);
    }

    @Nonnull
    @Override
    public AuthClient authClient(@Nonnull AuthClientInfo authClientInfo) {
        return AuthClientDO.create(authClientInfo);
    }

    @Nonnull
    @Override
    public AccessToken accessToken(@Nonnull AuthClient authClient,
                                   @Nonnull UserDetail userDetail,
                                   long sessionTimeout) {
        return AccessTokenDO.create(authClient, userDetail, sessionTimeout);
    }

}
