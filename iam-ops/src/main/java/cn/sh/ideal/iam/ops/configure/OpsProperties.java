package cn.sh.ideal.iam.ops.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2023/12/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ideal-iam.ops")
public class OpsProperties {

    /** IP白名单 */
    @Nonnull
    private Set<String> ipWhitelist = new HashSet<>();

}
