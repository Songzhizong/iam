package cn.sh.ideal.iam.authorization.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 宋志宗 on 2023/12/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ideal-iam.authorization")
public class AuthorizationProperties {

}
