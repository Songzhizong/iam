package cn.sh.ideal.iam.authorization.standard.domain.model;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AuthClient {

    Long getId();

    @Nonnull
    String  getPlatform();
}
