package cn.sh.ideal.iam.factor.otp.domain.model;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/29
 */
public interface EntityFactory {

    @Nonnull
    UserTotp userTotp(long userId, @Nonnull String secret);
}
