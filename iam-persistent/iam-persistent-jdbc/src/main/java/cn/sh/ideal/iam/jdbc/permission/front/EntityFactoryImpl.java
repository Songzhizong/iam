package cn.sh.ideal.iam.jdbc.permission.front;

import cn.sh.ideal.iam.permission.front.domain.model.*;
import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionGroupArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionItemArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionGroupInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionItemInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Component("frontEntityFactory")
public class EntityFactoryImpl implements EntityFactory {

    @Nonnull
    @Override
    public App app(long id, @Nonnull CreateAppArgs args) {
        return AppDO.create(id, args);
    }

    @Nonnull
    @Override
    public App app(@Nonnull AppInfo appInfo) {
        return AppDO.ofInfo(appInfo);
    }

    @Nonnull
    @Override
    public Permission permission(@Nonnull PermissionInfo permissionInfo) {
        return PermissionDO.ofInfo(permissionInfo);
    }

    @Nonnull
    @Override
    public Permission permission(long id, @Nonnull PermissionItem item, @Nonnull CreatePermissionArgs args) {
        return PermissionDO.create(id, item, args);
    }

    @Nonnull
    @Override
    public PermissionItem permissionItem(long id, @Nonnull PermissionGroup group, @Nonnull CreatePermissionItemArgs args) {
        return PermissionItemDO.create(id, group, args);
    }

    @Nonnull
    @Override
    public PermissionItem permissionItem(@Nonnull PermissionItemInfo permissionItemInfo) {
        return PermissionItemDO.ofInfo(permissionItemInfo);
    }

    @Nonnull
    @Override
    public PermissionGroup permissionGroup(long id, @Nonnull CreatePermissionGroupArgs args) {
        return PermissionGroupDO.create(id, args);
    }

    @Nonnull
    @Override
    public PermissionGroup permissionGroup(@Nonnull PermissionGroupInfo permissionGroupInfo) {
        return PermissionGroupDO.ofInfo(permissionGroupInfo);
    }

}
