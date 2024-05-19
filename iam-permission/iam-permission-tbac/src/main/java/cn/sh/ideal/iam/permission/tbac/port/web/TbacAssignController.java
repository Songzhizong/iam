package cn.sh.ideal.iam.permission.tbac.port.web;

import cn.idealio.framework.audit.Audit;
import cn.idealio.framework.audit.AuditAction;
import cn.idealio.framework.transmission.Result;
import cn.idealio.security.api.annotation.HasAuthority;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.permission.tbac.application.AssignService;
import cn.sh.ideal.iam.permission.tbac.dto.args.AssignPermissionArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/tbac")
public class TbacAssignController {
    private final AssignService assignService;

    /**
     * 批量分配权限
     * <pre>
     *   <b>需要权限: iam:tbac:assign_permission</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/tbac/assign_permission
     *   Content-Type: application/json
     *
     *   {
     *     "containerId": 1,
     *     "userGroupId": 622070529905393664,
     *     "permissionIds": [
     *       622429912589926400,
     *       622430129179590656,
     *       622430178206810112,
     *       622430236578938880,
     *       622430302093967360,
     *       622430574946025472,
     *       622430682223738880
     *     ],
     *     "assign": true,
     *     "extend": true,
     *     "mfa": false
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4qc912yk0dfk
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success"
     *   }
     * </pre>
     *
     * @param args 分配参数
     * @author 宋志宗 on 2024/5/17
     */
    @PostMapping("/assign_permission")
    @HasAuthority("iam:sc:assign_permission")
    @Audit(name = "分配权限", code = "iam:sc:assign_permission",
            action = AuditAction.PERMISSION_CONFIG, classification = AuditConstants.AUTHORITY_MANAGEMENT)
    public Result<Void> assignPermissions(@RequestBody AssignPermissionArgs args) {
        assignService.assign(args);
        return Result.success();
    }
}
