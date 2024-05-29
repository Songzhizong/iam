package cn.sh.ideal.iam.jdbc.factor.otp;

import cn.sh.ideal.iam.factor.otp.domain.model.UserTotp;
import cn.sh.ideal.iam.factor.otp.domain.model.UserTotpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Repository
@RequiredArgsConstructor
public class UserTotpRepositoryImpl implements UserTotpRepository {
    private final UserTotpJpaRepository userTotpJpaRepository;

    @Nonnull
    @Override
    public UserTotp insert(@Nonnull UserTotp userTotp) {
        UserTotpDO entity = (UserTotpDO) userTotp;
        return userTotpJpaRepository.save(entity);
    }

    @Override
    public void delete(@Nonnull UserTotp userTotp) {
        UserTotpDO entity = (UserTotpDO) userTotp;
        userTotpJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<UserTotp> findByUserId(@Nonnull Long userId) {
        return userTotpJpaRepository.findById(userId).map(e -> e);
    }
}
