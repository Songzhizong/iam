package cn.sh.ideal.iam.organization.port.web;

import cn.idealio.framework.audit.Audit;
import cn.idealio.framework.audit.AuditAction;
import cn.idealio.framework.transmission.Result;
import cn.idealio.framework.util.Asserts;
import cn.idealio.security.api.annotation.HasAuthority;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.organization.application.GroupService;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Group;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import cn.sh.ideal.iam.organization.dto.resp.GroupInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

/**
 * 用户组管理
 *
 * @author 宋志宗 on 2024/5/14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam")
public class GroupController {
    private final GroupService groupService;
    private final OrganizationI18nReader i18nReader;

    /**
     * 创建用户组
     * <pre>
     *   <b>需要权限: iam:user_group:create</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/user_groups?tenantId=622057660241412096
     *   Content-Type: application/json
     *
     *   {
     *     "containerId": 2,
     *     "name": "管理员用户组",
     *     "note": "管理员用户组"
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4q1bract0w
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success",
     *     "data": {
     *       "id": "622066105392824320",
     *       "tenantId": "622057660241412096",
     *       "containerId": "2",
     *       "name": "管理员用户组",
     *       "note": "管理员用户组"
     *     }
     *   }
     * </pre>
     *
     * @param tenantId 租户ID
     * @author 宋志宗 on 2024/5/15
     */
    @PostMapping("/user_groups")
    @HasAuthority("iam:user_group:create")
    @Audit(name = "新增用户组", code = AuditConstants.CREATE_USER_GROUP,
            action = AuditAction.CREATE, classification = AuditConstants.USER_GROUP)
    public Result<GroupInfo> create(@Nullable Long tenantId,
                                    @RequestBody CreateGroupArgs args) {
        Asserts.nonnull(tenantId, () -> i18nReader.getMessage("tenant.id.null"));
        Group group = groupService.create(tenantId, args);
        GroupInfo info = group.toInfo();
        return Result.success(info);
    }

    /**
     * 删除用户组
     * <pre>
     *   <b>需要权限: iam:user_group:delete</b>
     *   <p><b>请求示例</b></p>
     *   DELETE {{base_url}}/iam/user_groups/622057943822499840
     *
     *   <p><b>响应示例</b></p>
     *   见 {@link #create(Long, CreateGroupArgs)}
     * </pre>
     *
     * @param id 用户组ID
     * @author 宋志宗 on 2024/5/15
     */
    @Audit(name = "删除用户组", code = AuditConstants.DELETE_USER_GROUP,
            action = AuditAction.DELETE, classification = AuditConstants.USER_GROUP)
    @HasAuthority("iam:user_group:delete")
    @DeleteMapping("/user_groups/{id}")
    public Result<GroupInfo> delete(@PathVariable long id) {
        Group group = groupService.delete(id);
        if (group == null) {
            return Result.success();
        }
        GroupInfo info = group.toInfo();
        return Result.success(info);
    }
}
