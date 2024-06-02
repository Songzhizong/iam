package cn.sh.ideal.iam.permission.front.domain.model;

import cn.sh.ideal.iam.permission.front.dto.resp.PermissionGroupInfo;

import javax.annotation.Nonnull;

/**
 * 权限组
 *
 * @author 宋志宗 on 2024/5/16
 */
public interface PermissionGroup {

    @Nonnull
    Long getId();

    @Nonnull
    Long getAppId();

    @Nonnull
    String getName();

    boolean isEnabled();

    int getOrderNum();

    @Nonnull
    default PermissionGroupInfo toInfo() {
        PermissionGroupInfo permissionGroupInfo = new PermissionGroupInfo();
        permissionGroupInfo.setId(getId());
        permissionGroupInfo.setAppId(getAppId());
        permissionGroupInfo.setName(getName());
        permissionGroupInfo.setEnabled(isEnabled());
        permissionGroupInfo.setOrderNum(getOrderNum());
        return permissionGroupInfo;
    }
}
