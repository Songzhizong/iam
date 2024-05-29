package cn.sh.ideal.iam.factor.otp.domain.model;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/29
 */
public interface UserTotpRepository {

    @Nonnull
    UserTotp insert(@Nonnull UserTotp userTotp);

    void delete(@Nonnull UserTotp userTotp);

    @Nonnull
    Optional<UserTotp> findByUserId(@Nonnull Long userId);
}
