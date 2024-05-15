package cn.sh.ideal.iam.jdbc.configurer;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 宋志宗 on 2023/4/17
 */
@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan("cn.sh.ideal.iam.jdbc")
@ComponentScan("cn.sh.ideal.iam.jdbc")
@EnableJpaRepositories("cn.sh.ideal.iam.jdbc")
@ImportRuntimeHints(JdbcPersistentRuntimeHints.class)
public class JdbcPersistentAutoConfigurer {
}
