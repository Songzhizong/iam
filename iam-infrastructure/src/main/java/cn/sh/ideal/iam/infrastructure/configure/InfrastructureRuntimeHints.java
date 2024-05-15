package cn.sh.ideal.iam.infrastructure.configure;

import cn.sh.ideal.iam.infrastructure.extended.ExtendedAttributes;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2023/12/27
 */
public class InfrastructureRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@Nonnull RuntimeHints hints, ClassLoader classLoader) {
        List<TypeReference> references = List.of(
                TypeReference.of(ExtendedAttributes.class)
        );
        hints.reflection().registerTypes(references, b -> b.withMembers(MemberCategory.values()));
        hints.serialization().registerType(TypeReference.of(ExtendedAttributes.class))
        ;
    }
}
