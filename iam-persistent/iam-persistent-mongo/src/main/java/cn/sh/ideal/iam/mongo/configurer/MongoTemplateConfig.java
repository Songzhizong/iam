package cn.sh.ideal.iam.mongo.configurer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * @author 宋志宗 on 2023/3/31
 */
@Configuration
public class MongoTemplateConfig implements InitializingBean {
    private final MongoTemplate mongoTemplate;

    public MongoTemplateConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        // 移除 _class
        MongoConverter converter = mongoTemplate.getConverter();
        if (converter instanceof MappingMongoConverter mappingMongoConverter
                && converter.getTypeMapper().isTypeKey("_class")) {
            mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        }
    }
}
