package cn.sh.ideal.iam.permission.front.domain.model;

import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionGroupInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionItemInfo;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface EntityFactory {

    @Nonnull
    App app(@Nonnull CreateAppArgs args);

    @Nonnull
    App app(@Nonnull AppInfo appInfo);

    @Nonnull
    Permission permission(@Nonnull PermissionInfo permissionInfo);

    @Nonnull
    PermissionItem permissionItem(@Nonnull PermissionItemInfo permissionItemInfo);

    @Nonnull
    PermissionGroup permissionGroup(@Nonnull PermissionGroupInfo permissionGroupInfo);
}
