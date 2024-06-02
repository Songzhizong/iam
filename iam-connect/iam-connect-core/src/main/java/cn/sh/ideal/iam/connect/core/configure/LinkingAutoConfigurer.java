package cn.sh.ideal.iam.connect.core.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * @author 宋志宗 on 2023/12/27
 */
@ComponentScan("cn.sh.ideal.iam.connect.core")
@ImportRuntimeHints(LinkingRuntimeHints.class)
@EnableConfigurationProperties(LinkingProperties.class)
public class LinkingAutoConfigurer {

}
