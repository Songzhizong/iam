package cn.sh.ideal.iam.permission.front.configure;

import cn.sh.ideal.iam.permission.front.dto.resp.AppDetail;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2023/12/27
 */
public class FrontRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@Nonnull RuntimeHints hints, ClassLoader classLoader) {
        List<TypeReference> references = List.of(
                TypeReference.of(AppDetail.class)
        );
        hints.reflection().registerTypes(references, b -> b.withMembers(MemberCategory.values()));
    }
}
