package cn.sh.ideal.iam.authorization.standard.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * @author 宋志宗 on 2023/12/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ideal-iam.authorization.standard")
public class AuthorizationStandardProperties {

    /** 是否允许多地同时登录 */
    private boolean allowMultipleLogin = false;

    /** 登录会话有效期 */
    @Nonnull
    private Duration sessionTimeout = Duration.ofMinutes(30);
}
