package cn.sh.ideal.iam.infrastructure.user;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface UserLastActiveRepository {

    /** 更新最近活跃时间为当前时间 */
    void updateLastActiveTime(@Nonnull Long userId);

    /** 获取用户最近活跃时间 */
    @Nullable
    Long getLastActiveTime(@Nonnull Long userId);

    /** 批量获取用户最近活跃时间 */
    @Nonnull
    Map<Long, Long> getLastActiveTime(@Nonnull Collection<Long> userIds);
}
