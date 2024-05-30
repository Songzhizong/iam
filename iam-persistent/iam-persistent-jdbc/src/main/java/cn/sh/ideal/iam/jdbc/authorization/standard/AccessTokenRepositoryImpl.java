package cn.sh.ideal.iam.jdbc.authorization.standard;

import cn.sh.ideal.iam.authorization.standard.domain.model.AccessToken;
import cn.sh.ideal.iam.authorization.standard.domain.model.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Repository
@RequiredArgsConstructor
public class AccessTokenRepositoryImpl implements AccessTokenRepository {
    private final AccessTokenJpaRepository accessTokenJpaRepository;

    @Nonnull
    @Override
    public AccessToken insert(@Nonnull AccessToken accessToken) {
        AccessTokenDO entity = (AccessTokenDO) accessToken;
        return accessTokenJpaRepository.saveAndFlush(entity);
    }

    @Nonnull
    @Override
    public AccessToken update(@Nonnull AccessToken accessToken) {
        AccessTokenDO entity = (AccessTokenDO) accessToken;
        return accessTokenJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void deleteById(long id) {
        accessTokenJpaRepository.deleteById(id);
    }

    @Nonnull
    @Override
    public Optional<AccessToken> findById(long id) {
        return accessTokenJpaRepository.findById(id).map(it -> it);
    }
}
