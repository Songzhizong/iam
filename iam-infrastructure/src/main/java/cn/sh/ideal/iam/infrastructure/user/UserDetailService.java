package cn.sh.ideal.iam.infrastructure.user;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface UserDetailService {

    @Nullable
    UserDetail loadUserByUsername(@Nonnull String platform,
                                  @Nonnull String username,
                                  @Nonnull String password);

    @Nullable
    UserDetail findById(long userId);
}
