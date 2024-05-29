package cn.sh.ideal.iam.linking.oauth2.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * @author 宋志宗 on 2023/12/27
 */
@ComponentScan("cn.sh.ideal.iam.linking.oauth2")
@ImportRuntimeHints(LinkingOauth2RuntimeHints.class)
@EnableConfigurationProperties(LinkingOauth2Properties.class)
public class LinkingOauth2AutoConfigurer {

}
