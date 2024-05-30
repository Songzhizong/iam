package cn.sh.ideal.iam.authorization.standard.application;

import cn.idealio.framework.exception.ForbiddenException;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClient;
import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientRepository;
import cn.sh.ideal.iam.authorization.standard.domain.model.VisibleToken;
import cn.sh.ideal.iam.authorization.standard.dto.resp.LoginResponse;
import cn.sh.ideal.iam.authorization.standard.exception.UsernameOrPasswordIncorrectException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import cn.sh.ideal.iam.infrastructure.user.UserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final IamI18nReader i18nReader;
    private final UserDetailService userDetailService;
    private final AccessTokenService accessTokenService;
    private final AuthClientRepository authClientRepository;

    @Nonnull
    public LoginResponse passwordLogin(@Nonnull String username,
                                       @Nonnull String password,
                                       @Nonnull String clientToken) {
        AuthClient authClient = authClientRepository.findByToken(clientToken).orElseThrow(() -> {
            log.info("登录失败, 客户端Token无效: {}", clientToken);
            return new ForbiddenException(i18nReader.getMessage("login.client_token.invalid"));
        });
        String platform = authClient.getPlatform();
        UserDetail userDetail = userDetailService.loadUserByUsername(platform, username, password);
        if (userDetail == null) {
            log.info("登录失败, 返回用户信息为空: {}", username);
            throw new UsernameOrPasswordIncorrectException(0, 5);
        }
        return doLogin(authClient, userDetail);
    }

    @Nonnull
    public LoginResponse doLogin(@Nonnull AuthClient authClient,
                                 @Nonnull UserDetail userDetail) {
        if (userDetail.isBlocked()) {
            log.info("登录失败, 用户被禁用: {}", userDetail.getId());
            throw new ForbiddenException(i18nReader.getMessage("login.user.blocked"));
        }
        if (userDetail.isAccountExpired()) {
            log.info("登录失败, 用户账号已过期: {}", userDetail.getId());
            throw new ForbiddenException(i18nReader.getMessage("login.user.account_expired"));
        }
        VisibleToken token = accessTokenService.generate(authClient, userDetail);
        return LoginResponse.token(token);
    }
}
