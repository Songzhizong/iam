package cn.sh.ideal.iam.organization.port.web;

import cn.idealio.framework.audit.Audit;
import cn.idealio.framework.audit.AuditAction;
import cn.idealio.framework.transmission.Result;
import cn.idealio.framework.util.Asserts;
import cn.idealio.security.api.annotation.HasAuthority;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.organization.application.UserService;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;

/**
 * 用户管理
 *
 * @author 宋志宗 on 2024/5/14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam")
public class UserController {
    private final UserService userService;
    private final OrganizationI18nReader i18nReader;

    /**
     * 新增用户
     * <pre>
     *   <b>需要权限: iam:user:create</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/users?tenantId=622057660241412096
     *   Content-Type: application/json
     *
     *   {
     *     "containerId": 2,
     *     "name": "宋志宗",
     *     "account": "songzhizong",
     *     "phone": "18256928780",
     *     "email": "zzsong91@163.com",
     *     "language": "zh_CN",
     *     "userGroupIds": [
     *       622070529905393664
     *     ]
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4q589heyday
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success",
     *     "data": {
     *       "id": "622078047075434496",
     *       "tenantId": "622057660241412096",
     *       "containerId": "2",
     *       "name": "宋志宗",
     *       "account": "songzhizong",
     *       "phone": "18256928780",
     *       "email": "zzsong91@163.com",
     *       "language": "zh_CN"
     *     }
     *   }
     * </pre>
     *
     * @param tenantId 租户ID
     * @author 宋志宗 on 2024/5/16
     */
    @PostMapping("/users")
    @HasAuthority("iam:user:create")
    @Audit(name = "新增用户", code = AuditConstants.CREATE_USER,
            action = AuditAction.CREATE, classification = AuditConstants.USER)
    public Result<User> create(@Nullable Long tenantId, @RequestBody CreateUserArgs args) {
        Asserts.nonnull(tenantId, () -> i18nReader.getMessage("tenant.id.null"));
        User user = userService.create(tenantId, args);
        return Result.success(user);
    }
}
