package cn.sh.ideal.iam.authorization.standard.domain.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AuthClientRepository {

    @Nonnull
    AuthClient insert(@Nonnull AuthClient authClient);

    @Nonnull
    AuthClient update(@Nonnull AuthClient authClient);

    void delete(@Nonnull AuthClient authClient);

    @Nonnull
    Optional<AuthClient> findById(long id);

    @Nonnull
    Optional<AuthClient> findByToken(@Nonnull String token);

    @Nonnull
    List<AuthClient> findAllByPlatform(@Nonnull String platform);
}
