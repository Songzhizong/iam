package cn.sh.ideal.iam.permission.front.domain.model;

import cn.sh.ideal.iam.permission.front.dto.resp.PermissionItemInfo;

import javax.annotation.Nonnull;

/**
 * 权限项
 *
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionItem {

    long getId();

    long getAppId();

    long getGroupId();

    @Nonnull
    String getName();

    boolean isEnabled();

    int getOrderNum();

    @Nonnull
    default PermissionItemInfo toInfo() {
        PermissionItemInfo permissionItemInfo = new PermissionItemInfo();
        permissionItemInfo.setId(getId());
        permissionItemInfo.setAppId(getAppId());
        permissionItemInfo.setGroupId(getGroupId());
        permissionItemInfo.setName(getName());
        permissionItemInfo.setEnabled(isEnabled());
        permissionItemInfo.setOrderNum(getOrderNum());
        return permissionItemInfo;

    }
}
