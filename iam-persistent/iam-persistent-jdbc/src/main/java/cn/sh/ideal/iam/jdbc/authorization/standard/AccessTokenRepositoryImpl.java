package cn.sh.ideal.iam.jdbc.authorization.standard;

import cn.idealio.framework.spring.SpringPageConverter;
import cn.idealio.framework.transmission.Paging;
import cn.sh.ideal.iam.authorization.standard.domain.model.AccessToken;
import cn.sh.ideal.iam.authorization.standard.domain.model.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
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

    @Override
    public void update(@Nonnull AccessToken accessToken) {
        AccessTokenDO entity = (AccessTokenDO) accessToken;
        accessTokenJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void deleteById(@Nonnull Long id) {
        accessTokenJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(@Nonnull Collection<Long> ids) {
        accessTokenJpaRepository.deleteAllByIdIn(ids);
    }

    @Nonnull
    @Override
    public Optional<AccessToken> findById(@Nonnull Long id) {
        return accessTokenJpaRepository.findById(id).map(it -> it);
    }

    @Nonnull
    @Override
    public List<AccessToken> findAllByUserIdAndClientId(@Nonnull Long userId,
                                                        @Nonnull Long clientId,
                                                        @Nonnull Paging paging) {
        Pageable pageable = SpringPageConverter.pageable(paging);
        return accessTokenJpaRepository.findAllByUserIdAndClientId(
                userId, clientId, pageable
        ).stream().map(e -> (AccessToken) e).toList();
    }
}
