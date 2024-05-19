package cn.sh.ideal.iam.permission.tbac.port.web;

import cn.idealio.framework.audit.Audit;
import cn.idealio.framework.audit.AuditAction;
import cn.idealio.framework.transmission.ListResult;
import cn.idealio.framework.transmission.Result;
import cn.idealio.security.api.annotation.HasAuthority;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.permission.tbac.application.SecurityContainerService;
import cn.sh.ideal.iam.permission.tbac.domain.model.SecurityContainer;
import cn.sh.ideal.iam.permission.tbac.dto.args.CreateSecurityContainerArgs;
import cn.sh.ideal.iam.permission.tbac.dto.resp.SecurityContainerInfo;
import cn.sh.ideal.iam.permission.tbac.dto.resp.SecurityContainerTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * 安全容器管理
 *
 * @author 宋志宗 on 2024/5/14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam")
public class SecurityContainerController {
    private final SecurityContainerService securityContainerService;

    /**
     * 新建安全容器
     * <pre>
     *   <b>需要权限: iam:security_container:create</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/security_containers
     *   Content-Type: application/json
     *
     *   {
     *     "parentId": 1,
     *     "name": "北京小米"
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
     *       "id": "3",
     *       "parentId": "1",
     *       "name": "北京小米"
     *     }
     *   }
     * </pre>
     *
     * @author 宋志宗 on 2024/5/14
     */
    @PostMapping("/security_containers")
    @HasAuthority("iam:security_container:create")
    @Audit(name = "新增安全容器", code = AuditConstants.CREATE_SECURITY_CONTAINER,
            action = AuditAction.CREATE, classification = AuditConstants.SECURITY_CONTAINER)
    public Result<SecurityContainerInfo> create(@RequestBody CreateSecurityContainerArgs args) {
        SecurityContainer securityContainer = securityContainerService.create(args);
        SecurityContainerInfo info = securityContainer.toInfo();
        return Result.success(info);
    }

    /**
     * 重命名安全容器
     * <pre>
     *   <b>需要权限: iam:security_container:update</b>
     *   <p><b>请求示例</b></p>
     *   PATCH {{base_url}}/iam/security_containers/3/rename?name=理想南京
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
     *       "id": "3",
     *       "parentId": "1",
     *       "name": "理想南京"
     *     }
     *   }
     * </pre>
     *
     * @param id   安全容器ID
     * @param name 新的安全容器名称
     * @author 宋志宗 on 2024/5/14
     */
    @PatchMapping("/security_containers/{id}/rename")
    @HasAuthority("iam:security_container:update")
    @Audit(name = "重命名安全容器", code = AuditConstants.RENAME_SECURITY_CONTAINER,
            action = AuditAction.UPDATE, classification = AuditConstants.SECURITY_CONTAINER)
    public Result<SecurityContainerInfo> rename(@PathVariable Long id, @Nullable String name) {
        SecurityContainer securityContainer = securityContainerService.rename(id, name);
        SecurityContainerInfo info = securityContainer.toInfo();
        return Result.success(info);
    }

    @PatchMapping("/security_containers/{id}/move")
    @HasAuthority("iam:security_container:update")
    @Audit(name = "移动安全容器", code = AuditConstants.MOVE_SECURITY_CONTAINER,
            action = AuditAction.UPDATE, classification = AuditConstants.SECURITY_CONTAINER)
    public Result<Void> move(@PathVariable long id, @Nullable Long parentId) {
        securityContainerService.changeParent(id, parentId);
        return Result.success();
    }

    @DeleteMapping("/security_containers/{id}")
    @HasAuthority("iam:security_container:delete")
    @Audit(name = "删除安全容器", code = AuditConstants.DELETE_SECURITY_CONTAINER,
            action = AuditAction.UPDATE, classification = AuditConstants.SECURITY_CONTAINER)
    public Result<Void> delete(@PathVariable long id) {
        securityContainerService.delete(id);
        return Result.success();
    }


    @GetMapping("/security_containers/{id}/deletable")
    public Result<Boolean> deletable(@PathVariable long id) {
        boolean deletable = securityContainerService.deletable(id);
        return Result.success(deletable);
    }

    @GetMapping("/security_containers/{id}/exists_resource_types")
    public Result<Set<String>> resourceTypes(@PathVariable long id) {
        Set<String> resourceTypes = securityContainerService.existsResourceTypes(id);
        return Result.success(resourceTypes);
    }
}
