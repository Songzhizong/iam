package cn.sh.ideal.iam.authorization.configure;

import cn.sh.ideal.iam.authorization.domain.model.Authentication;
import cn.sh.ideal.iam.authorization.domain.model.VisibleToken;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2023/12/27
 */
public class AuthorizationRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@Nonnull RuntimeHints hints, ClassLoader classLoader) {
        List<TypeReference> references = List.of(
                TypeReference.of(Authentication.class),
                TypeReference.of(VisibleToken.class)
        );
        hints.reflection().registerTypes(references, b -> b.withMembers(MemberCategory.values()));
        hints.resources().registerPattern("i18n/**");
    }
}
