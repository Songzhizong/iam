package cn.sh.ideal.iam.permission.core.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 宋志宗 on 2023/12/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ideal-iam.permission")
public class PermissionProperties {

}
