package cn.sh.ideal.iam.authorization.core;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/29
 */
public interface Authorization {

    @Nonnull
    String getType();
}
