package cn.sh.ideal.iam.authorization.standard.dto.resp;

import cn.sh.ideal.iam.authorization.standard.domain.model.VisibleToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponse {
    private final Type type;
    @Nullable
    private VisibleToken token;

    @Nonnull
    public static LoginResponse token(@Nonnull VisibleToken token) {
        LoginResponse loginResponse = new LoginResponse(Type.TOKEN);
        loginResponse.setToken(token);
        return loginResponse;
    }

    public enum Type {
        /** 成功执行登录 */
        TOKEN,

        /** 登录成功, 但需要进行多因素认证 */
        NEED_MFA,

        /** 登录成功, 但是密码已过期, 需要修改密码 */
        PASSWORD_EXPIRED,

        /** 登录成功, 但是密码不合规, 需要修改密码 */
        PASSWORD_ILLEGAL,

        /** 登录请求匹配到多个账号, 需要选择其中一个执行登录动作 */
        SELECT_ACCOUNT,
    }
}
