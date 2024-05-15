package cn.sh.ideal.iam.infrastructure.configure;

import cn.idealio.framework.generator.IDGenerator;
import cn.idealio.framework.generator.IDGeneratorFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2023/4/24
 */
@Component
public class IamIDGenerator implements IDGenerator {
    private final IDGenerator idGenerator;

    public IamIDGenerator(@Nonnull IDGeneratorFactory idGeneratorFactory) {
        this.idGenerator = idGeneratorFactory.getGenerator("ideal.iam");
    }


    @Override
    public long generate() {
        return idGenerator.generate();
    }
}
