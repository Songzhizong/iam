package cn.sh.ideal.iam.authorization.standard.domain.model;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface AccessTokenRepository {

    @Nonnull
    AccessToken insert(@Nonnull AccessToken accessToken);

    @Nonnull
    AccessToken update(@Nonnull AccessToken accessToken);

    void deleteById(@Nonnull Long id);

    @Nonnull
    Optional<AccessToken> findById(@Nonnull Long id);
}
