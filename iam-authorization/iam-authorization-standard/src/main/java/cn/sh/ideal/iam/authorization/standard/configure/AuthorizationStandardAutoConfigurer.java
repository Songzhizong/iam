package cn.sh.ideal.iam.authorization.standard.configure;

import cn.idealio.framework.autoconfigure.cache.EnableCache;
import cn.idealio.framework.autoconfigure.lock.EnableGlobalLock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * @author 宋志宗 on 2023/12/27
 */
@EnableCache
@EnableGlobalLock
@ComponentScan("cn.sh.ideal.iam.authorization.standard")
@ImportRuntimeHints(AuthorizationStandardRuntimeHints.class)
@EnableConfigurationProperties(AuthorizationStandardProperties.class)
public class AuthorizationStandardAutoConfigurer {

}
