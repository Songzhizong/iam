package cn.sh.ideal.iam.permission.tbac.domain.model;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface PermissionAssign {

    @Nonnull
    Long getAppId();

    @Nonnull
    Long getContainerId();

    @Nonnull
    Long getPermissionId();

    boolean isAssigned();

    boolean isInheritable();

    boolean isMfa();
}
