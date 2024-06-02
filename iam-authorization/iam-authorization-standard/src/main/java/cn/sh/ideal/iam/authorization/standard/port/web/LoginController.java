package cn.sh.ideal.iam.authorization.standard.port.web;

import cn.idealio.framework.transmission.Result;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.authorization.standard.application.LoginService;
import cn.sh.ideal.iam.authorization.standard.dto.args.PasswordLoginArgs;
import cn.sh.ideal.iam.authorization.standard.dto.resp.LoginResponse;
import cn.sh.ideal.iam.common.constant.IamHeaders;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 登录相关接口
 *
 * @author 宋志宗 on 2024/5/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/login")
public class LoginController {
    private final IamI18nReader i18nReader;
    private final LoginService loginService;

    /**
     * 密码登录
     *
     * @param clientToken 客户端token
     */
    @PostMapping("/password")
    public Result<LoginResponse> login(
            @RequestHeader(name = IamHeaders.CLIENT_TOKEN, required = false)
            @Nullable String clientToken,
            @Nonnull @RequestBody PasswordLoginArgs args) {
        String username = args.getUsername();
        String password = args.rawPassword();
        Asserts.notBlank(username, () -> i18nReader.getMessage("login.username.required"));
        Asserts.notBlank(password, () -> i18nReader.getMessage("login.password.required"));
        Asserts.notBlank(clientToken, () -> i18nReader.getMessage("login.client_token.required"));
        LoginResponse response = loginService.passwordLogin(username, password, clientToken);
        return Result.success(response);
    }
}
