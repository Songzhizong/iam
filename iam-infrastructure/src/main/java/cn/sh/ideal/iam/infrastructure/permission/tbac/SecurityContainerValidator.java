package cn.sh.ideal.iam.infrastructure.permission.tbac;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface SecurityContainerValidator {

    void requireExits(@Nonnull Long containerId);
}
