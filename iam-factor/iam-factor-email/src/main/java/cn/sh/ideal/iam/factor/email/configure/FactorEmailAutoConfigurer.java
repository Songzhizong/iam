package cn.sh.ideal.iam.factor.email.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * @author 宋志宗 on 2023/12/27
 */
@ComponentScan("cn.sh.ideal.iam.factor.email")
@ImportRuntimeHints(FactorEmailRuntimeHints.class)
@EnableConfigurationProperties(FactorEmailProperties.class)
public class FactorEmailAutoConfigurer {

}
