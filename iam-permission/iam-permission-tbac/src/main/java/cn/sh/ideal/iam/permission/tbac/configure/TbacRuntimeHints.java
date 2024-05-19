package cn.sh.ideal.iam.permission.tbac.configure;

import cn.sh.ideal.iam.permission.tbac.domain.model.AnalyzedSecurityContainer;
import cn.sh.ideal.iam.permission.tbac.domain.model.AssignedPermission;
import cn.sh.ideal.iam.permission.tbac.domain.model.PermissionAssignDetail;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2023/12/27
 */
public class TbacRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@Nonnull RuntimeHints hints, ClassLoader classLoader) {
        List<TypeReference> references = List.of(
                TypeReference.of(AnalyzedSecurityContainer.class),
                TypeReference.of(AssignedPermission.class),
                TypeReference.of(PermissionAssignDetail.class)
        );
        hints.reflection().registerTypes(references, b -> b.withMembers(MemberCategory.values()));
        hints.resources().registerPattern("i18n/**");
    }
}
