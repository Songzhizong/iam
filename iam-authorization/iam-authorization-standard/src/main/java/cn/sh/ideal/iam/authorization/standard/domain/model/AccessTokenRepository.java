package cn.sh.ideal.iam.authorization.standard.domain.model;

import cn.idealio.framework.transmission.Paging;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface AccessTokenRepository {

    @Nonnull
    AccessToken insert(@Nonnull AccessToken accessToken);

    void update(@Nonnull AccessToken accessToken);

    void deleteById(@Nonnull Long id);

    void deleteAllById(@Nonnull Collection<Long> ids);

    @Nonnull
    Optional<AccessToken> findById(@Nonnull Long id);

    @Nonnull
    List<AccessToken> findAllByUserIdAndClientId(@Nonnull Long userId,
                                                 @Nonnull Long clientId,
                                                 @Nonnull Paging paging);
}
