package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface TenantContainerChangeLog {

    /** 获取租户ID */
    long getTenantId();

    /** 获取原有的安全容器ID */
    @Nullable
    Long getOldContainerId();

    /** 获取新的安全容器ID */
    @Nullable
    Long getNewContainerId();
}
