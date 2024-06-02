package cn.sh.ideal.iam.security.core.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/12/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ideal-iam.security")
public class SecurityProperties {

    /**
     * 是否开启接口鉴权, 默认是
     */
    private boolean enableApiAuthenticate = true;


    /** 不需要登录认证的接口清单 */
    @Nonnull
    private Set<String> permitMatchers = new LinkedHashSet<>();

    /** 不需要租户校验的接口清单 */
    @Nonnull
    private Set<String> tenantAccessMatchers = new LinkedHashSet<>();
}
