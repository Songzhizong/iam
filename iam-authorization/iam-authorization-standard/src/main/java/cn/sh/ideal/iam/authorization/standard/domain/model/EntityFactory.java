package cn.sh.ideal.iam.authorization.standard.domain.model;

import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/30
 */
public interface EntityFactory {

    @Nonnull
    AuthClient authClient(@Nonnull CreateAuthClientArgs args);

    @Nonnull
    AccessToken accessToken(@Nonnull AuthClient authClient,
                            @Nonnull UserDetail userDetail,
                            long sessionTimeout);
}
