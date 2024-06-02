package cn.sh.ideal.iam.authorization.standard.domain.model;

import cn.sh.ideal.iam.common.constant.Terminal;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface AuthClient {

    @Nonnull
    Long getId();

    @Nonnull
    String getPlatform();

    @Nonnull
    String getName();

    @Nonnull
    String getNote();

    @Nonnull
    Terminal getTerminal();

    @Nonnull
    String getToken();

    @Nonnull
    default AuthClientInfo toInfo() {
        AuthClientInfo authClientInfo = new AuthClientInfo();
        authClientInfo.setId(getId());
        authClientInfo.setPlatform(getPlatform());
        authClientInfo.setName(getName());
        authClientInfo.setNote(getNote());
        authClientInfo.setTerminal(getTerminal());
        authClientInfo.setToken(getToken());
        return authClientInfo;
    }
}
