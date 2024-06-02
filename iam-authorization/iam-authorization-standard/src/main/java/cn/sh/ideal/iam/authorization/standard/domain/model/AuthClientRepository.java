package cn.sh.ideal.iam.authorization.standard.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface AuthClientRepository {

    @Nonnull
    AuthClient insert(@Nonnull AuthClient authClient);

    void insert(@Nonnull Collection<AuthClient> clients);

    @Nonnull
    AuthClient update(@Nonnull AuthClient authClient);

    void delete(@Nonnull AuthClient authClient);

    int deleteAllByPlatform(@Nonnull String platform);

    @Nonnull
    Optional<AuthClient> findById(@Nonnull Long id);

    @Nonnull
    Optional<AuthClient> findByToken(@Nonnull String token);

    @Nonnull
    List<AuthClient> findAllByPlatform(@Nonnull String platform);
}
