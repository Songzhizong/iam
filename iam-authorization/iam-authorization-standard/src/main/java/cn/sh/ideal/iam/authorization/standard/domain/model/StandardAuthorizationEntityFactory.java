package cn.sh.ideal.iam.authorization.standard.domain.model;

import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/30
 */
public interface StandardAuthorizationEntityFactory {

    @Nonnull
    AuthClient authClient(@Nonnull Long id,@Nonnull CreateAuthClientArgs args);

    @Nonnull
    AuthClient authClient(@Nonnull AuthClientInfo authClientInfo);

    @Nonnull
    AccessToken accessToken(@Nonnull AuthClient authClient,
                            @Nonnull UserDetail userDetail,
                            long sessionTimeout);
}
