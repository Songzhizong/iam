package cn.sh.ideal.iam.permission.front.port.web;

import cn.idealio.framework.transmission.Result;
import cn.sh.ideal.iam.permission.front.application.PermissionService;
import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionGroup;
import cn.sh.ideal.iam.permission.front.domain.model.PermissionItem;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionGroupArgs;
import cn.sh.ideal.iam.permission.front.dto.args.CreatePermissionItemArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionGroupInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionInfo;
import cn.sh.ideal.iam.permission.front.dto.resp.PermissionItemInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限管理
 *
 * @author 宋志宗 on 2024/5/16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/front")
public class PermissionController {
    private final PermissionService permissionService;

    /**
     * 新增权限分组
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/front/permission_groups
     *   Content-Type: application/json
     *
     *   {
     *     "appId": 622416657016422400,
     *     "name": "资产管理",
     *     "orderNum": 1,
     *     "enabled": true
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4q8oek7async8
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success",
     *     "data": {
     *       "id": "622428262412320768",
     *       "appId": "622416657016422400",
     *       "name": "资产管理",
     *       "enabled": true,
     *       "orderNum": 1
     *     }
     *   }
     * </pre>
     *
     * @author 宋志宗 on 2024/5/16
     */
    @PostMapping("/permission_groups")
    public Result<PermissionGroupInfo> createGroup(@RequestBody CreatePermissionGroupArgs args) {
        PermissionGroup group = permissionService.createGroup(args);
        PermissionGroupInfo info = group.toInfo();
        return Result.success(info);
    }

    /**
     * 新增权限配置项
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/front/permission_items
     *   Content-Type: application/json
     *
     *   {
     *     "groupId": 622428262412320768,
     *     "name": "设备",
     *     "orderNum": 0,
     *     "enabled": true
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4q8oj9wb4lc0
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success",
     *     "data": {
     *       "id": "622428631611736064",
     *       "appId": "622416657016422400",
     *       "groupId": "622428262412320768",
     *       "name": "设备",
     *       "enabled": true,
     *       "orderNum": 0
     *     }
     *   }
     * </pre>
     *
     * @author 宋志宗 on 2024/5/16
     */
    @PostMapping("/permission_items")
    public Result<PermissionItemInfo> createItem(@RequestBody CreatePermissionItemArgs args) {
        PermissionItem item = permissionService.createItem(args);
        PermissionItemInfo info = item.toInfo();
        return Result.success(info);
    }

    /**
     * 新增权限点
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *
     *   <p><b>响应示例</b></p>
     * </pre>
     *
     * @author 宋志宗 on 2024/5/16
     */
    @PostMapping("/permissions")
    public Result<PermissionInfo> createPermission(@RequestBody CreatePermissionArgs args) {
        Permission permission = permissionService.createPermission(args);
        PermissionInfo info = permission.toInfo();
        return Result.success(info);
    }
}
