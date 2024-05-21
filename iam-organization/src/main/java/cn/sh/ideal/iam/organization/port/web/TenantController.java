package cn.sh.ideal.iam.organization.port.web;

import cn.idealio.framework.audit.Audit;
import cn.idealio.framework.audit.AuditAction;
import cn.idealio.framework.audit.Audits;
import cn.idealio.framework.transmission.Result;
import cn.idealio.security.api.annotation.HasAuthority;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.organization.application.TenantService;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import cn.sh.ideal.iam.organization.dto.resp.TenantInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 租户管理
 *
 * @author 宋志宗 on 2024/5/14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam")
public class TenantController {
    private final TenantService tenantService;

    /**
     * 新增租户
     * <pre>
     *   <b>需要权限: iam:tenant:create</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/tenants
     *   Content-Type: application/json
     *
     *   {
     *     "containerId": 1,
     *     "name": "上海理想",
     *     "abbreviation": "ideal",
     *     "note": "",
     *     "systemEdition": "base"
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
     *       "id": "621681699385769984",
     *       "containerId": "1",
     *       "name": "上海理想",
     *       "abbreviation": "ideal",
     *       "note": "",
     *       "systemEdition": "base"
     *     }
     *   }
     * </pre>
     *
     * @author 宋志宗 on 2024/5/14
     */
    @PostMapping("/tenants")
    @HasAuthority("iam:tenant:create")
    @Audit(name = "新增租户", code = AuditConstants.CREATE_TENANT,
            action = AuditAction.CREATE, classification = AuditConstants.TENANT)
    public Result<TenantInfo> create(@RequestBody CreateTenantArgs args) {
        Tenant tenant = tenantService.create(args);
        TenantInfo tenantInfo = tenant.toInfo();
        Result<TenantInfo> result = Result.success(tenantInfo);
        Audits.modify(audit -> {
            audit.request(Map.of("args", args));
            audit.response(result);
        });
        return result;
    }
}
