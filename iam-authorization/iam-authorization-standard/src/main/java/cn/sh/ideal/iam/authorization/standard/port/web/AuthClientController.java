package cn.sh.ideal.iam.authorization.standard.port.web;

import cn.idealio.framework.transmission.Result;
import cn.sh.ideal.iam.authorization.standard.application.AuthClientService;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.dto.args.CreateAuthClientArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权端管理
 *
 * @author 宋志宗 on 2024/5/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam")
public class AuthClientController {
    private final AuthClientService authClientService;

    /**
     * 创建授权端
     *
     * @author 宋志宗 on 2024/5/30
     */
    @PostMapping("/auth_clients")
    public Result<AuthClient> create(@RequestBody CreateAuthClientArgs args) {
        AuthClient authClient = authClientService.create(args);
        return Result.success(authClient);
    }
}
