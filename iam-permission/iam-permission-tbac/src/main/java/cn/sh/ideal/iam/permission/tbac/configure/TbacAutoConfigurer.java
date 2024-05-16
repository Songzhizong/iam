package cn.sh.ideal.iam.permission.tbac.configure;

import cn.idealio.framework.autoconfigure.lock.EnableGlobalLock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * @author 宋志宗 on 2023/12/27
 */
@EnableGlobalLock
@ComponentScan("cn.sh.ideal.iam.permission.tbac")
@ImportRuntimeHints(TbacRuntimeHints.class)
@EnableConfigurationProperties(TbacProperties.class)
public class TbacAutoConfigurer {

}
