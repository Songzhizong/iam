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
     * 是否强制进行安全验证, 默认开启, 本地开发时可以关闭该选项
     */
    private boolean require = true;


    /** 不需要登录认证的接口清单 */
    @Nonnull
    private Set<String> permitMatchers = new LinkedHashSet<>();
}
