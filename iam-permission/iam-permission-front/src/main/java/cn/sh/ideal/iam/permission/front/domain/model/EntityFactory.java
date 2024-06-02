package cn.sh.ideal.iam.permission.front.domain.model;

import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionGroupArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionItemArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionGroupInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionItemInfo;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface EntityFactory {

    @Nonnull
    App app(@Nonnull Long id, @Nonnull CreateAppArgs args);

    @Nonnull
    App app(@Nonnull AppInfo appInfo);

    @Nonnull
    Permission permission(@Nonnull PermissionInfo permissionInfo);

    @Nonnull
    Permission permission(@Nonnull Long id, @Nonnull PermissionItem item, @Nonnull CreatePermissionArgs args);

    @Nonnull
    PermissionItem permissionItem(@Nonnull PermissionItemInfo permissionItemInfo);


    @Nonnull
    PermissionItem permissionItem(@Nonnull Long id, @Nonnull PermissionGroup group, @Nonnull CreatePermissionItemArgs args);

    @Nonnull
    PermissionGroup permissionGroup(@Nonnull PermissionGroupInfo permissionGroupInfo);

    @Nonnull
    PermissionGroup permissionGroup(@Nonnull Long id, @Nonnull CreatePermissionGroupArgs args);

}
