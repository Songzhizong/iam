package cn.sh.ideal.iam.mongo.configurer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 宋志宗 on 2023/4/17
 */
@EnableMongoAuditing
@EnableTransactionManagement
@EntityScan("cn.sh.ideal.iam.mongo")
@ComponentScan("cn.sh.ideal.iam.mongo")
@ImportRuntimeHints(MongoPersistentRuntimeHints.class)
public class MongoPersistentAutoConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }
}
