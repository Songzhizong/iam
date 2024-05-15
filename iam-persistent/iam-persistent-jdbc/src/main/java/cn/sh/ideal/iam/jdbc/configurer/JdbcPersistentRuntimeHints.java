package cn.sh.ideal.iam.jdbc.configurer;

import cn.sh.ideal.iam.jdbc.organization.GroupDO;
import cn.sh.ideal.iam.jdbc.organization.SecurityContainerDO;
import cn.sh.ideal.iam.jdbc.organization.TenantDO;
import cn.sh.ideal.iam.jdbc.organization.UserDO;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2023/4/15
 */
public class JdbcPersistentRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@Nonnull RuntimeHints hints, ClassLoader classLoader) {
        List<TypeReference> references = List.of(
                TypeReference.of(GroupDO.class),
                TypeReference.of(SecurityContainerDO.class),
                TypeReference.of(TenantDO.class),
                TypeReference.of(UserDO.class)
        );
        hints.reflection().registerTypes(
                references, builder -> builder.withMembers(MemberCategory.values())
        );
        hints.resources().registerPattern("common-service-persistent-jdbc/db_init_sql/*.sql");
    }
}
