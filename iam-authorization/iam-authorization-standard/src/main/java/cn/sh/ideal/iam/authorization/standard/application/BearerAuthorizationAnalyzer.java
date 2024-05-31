package cn.sh.ideal.iam.authorization.standard.application;

import cn.sh.ideal.iam.authorization.core.AuthorizationAnalyzer;
import cn.sh.ideal.iam.authorization.standard.domain.model.AccessToken;
import cn.sh.ideal.iam.authorization.standard.domain.model.BearerAuthorization;
import cn.sh.ideal.iam.infrastructure.user.UserDetail;
import cn.sh.ideal.iam.infrastructure.user.UserDetailService;
import cn.sh.ideal.iam.security.api.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BearerAuthorizationAnalyzer implements AuthorizationAnalyzer {
    private final UserDetailService userDetailService;
    private final AccessTokenService accessTokenService;

    @Override
    public boolean supports(@Nonnull String authorization) {
        return BearerAuthorization.supports(authorization);
    }

    @Override
    public Authentication analyze(@Nonnull String authorization) {
        BearerAuthorization bearerAuthorization = BearerAuthorization.read(authorization);
        long accessId = bearerAuthorization.getAccessId();
        AccessToken accessToken = accessTokenService.get(accessId);
        return new BearerAuthentication(accessToken, userDetailService);
    }

    @RequiredArgsConstructor
    public static class BearerAuthentication implements Authentication {
        private final AccessToken accessToken;
        private final UserDetailService userDetailService;
        @Nullable
        private String name = null;
        @Nullable
        private String account = null;

        @Override
        public long userId() {
            return accessToken.getUserId();
        }

        @Override
        public long tenantId() {
            return accessToken.getTenantId();
        }

        @Nullable
        @Override
        public String name() {
            String name = this.name;
            if (name != null) {
                return name;
            }
            UserDetail detail = userDetailService.findById(userId());
            if (detail == null) {
                this.name = "";
                this.account = "";
                return "";
            }
            name = detail.getName();
            this.name = name;
            this.account = detail.getAccount();
            return name;
        }

        @Nullable
        @Override
        public String account() {
            String account = this.account;
            if (account != null) {
                return account;
            }
            UserDetail detail = userDetailService.findById(userId());
            if (detail == null) {
                this.name = "";
                this.account = "";
                return "";
            }
            account = detail.getAccount();
            this.account = account;
            this.name = detail.getName();
            return account;
        }
    }
}
