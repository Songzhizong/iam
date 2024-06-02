package cn.sh.ideal.iam.organization.domain.model;

import cn.sh.ideal.iam.organization.dto.resp.GroupInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface UserGroup {

    Long getId();

    /** 所属租户ID */
    @Nonnull
    Long getTenantId();

    /** 安全容器ID */
    @Nullable
    Long getContainerId();

    @Nonnull
    String getName();

    @Nonnull
    String getNote();

    @Nonnull
    default GroupInfo toInfo() {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setId(getId());
        groupInfo.setTenantId(getTenantId());
        groupInfo.setContainerId(getContainerId());
        groupInfo.setName(getName());
        groupInfo.setNote(getNote());
        return groupInfo;
    }
}
