package cn.sh.ideal.iam.jdbc.authorization.standard;

import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Repository
@RequiredArgsConstructor
public class AuthClientRepositoryImpl implements AuthClientRepository {
    private final AuthClientJpaRepository authClientJpaRepository;

    @Nonnull
    @Override
    public AuthClient insert(@Nonnull AuthClient authClient) {
        AuthClientDO entity = (AuthClientDO) authClient;
        return authClientJpaRepository.saveAndFlush(entity);
    }

    @Nonnull
    @Override
    public AuthClient update(@Nonnull AuthClient authClient) {
        AuthClientDO entity = (AuthClientDO) authClient;
        return authClientJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(@Nonnull AuthClient authClient) {
        AuthClientDO entity = (AuthClientDO) authClient;
        authClientJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<AuthClient> findById(long id) {
        return authClientJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public Optional<AuthClient> findByToken(@Nonnull String token) {
        return authClientJpaRepository.findByToken(token).map(e -> e);
    }

    @Nonnull
    @Override
    public List<AuthClient> findAllByPlatform(@Nonnull String platform) {
        return authClientJpaRepository.findAllByPlatform(platform)
                .stream().map(e -> (AuthClient) e).toList();
    }
}
