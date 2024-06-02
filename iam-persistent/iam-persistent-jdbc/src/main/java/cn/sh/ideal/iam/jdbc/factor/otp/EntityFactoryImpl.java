package cn.sh.ideal.iam.jdbc.factor.otp;

import cn.sh.ideal.iam.factor.otp.domain.model.EntityFactory;
import cn.sh.ideal.iam.factor.otp.domain.model.UserTotp;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/29
 */
@Component("factorOtpEntityFactory")
public class EntityFactoryImpl implements EntityFactory {

    @Nonnull
    @Override
    public UserTotp userTotp(@Nonnull Long userId, @Nonnull String secret) {
        return UserTotpDO.create(userId, secret);
    }
}
