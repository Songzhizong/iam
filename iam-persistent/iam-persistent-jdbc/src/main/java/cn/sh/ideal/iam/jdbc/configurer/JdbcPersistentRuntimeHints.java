package cn.sh.ideal.iam.jdbc.configurer;

import cn.sh.ideal.iam.jdbc.organization.*;
import cn.sh.ideal.iam.jdbc.permission.front.AppDO;
import cn.sh.ideal.iam.jdbc.permission.front.PermissionDO;
import cn.sh.ideal.iam.jdbc.permission.front.PermissionGroupDO;
import cn.sh.ideal.iam.jdbc.permission.front.PermissionItemDO;
import cn.sh.ideal.iam.jdbc.permission.rbac.RbacPermissionAssignDO;
import cn.sh.ideal.iam.jdbc.permission.tbac.SecurityContainerDO;
import cn.sh.ideal.iam.jdbc.permission.tbac.TbacPermissionAssignDO;
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
                // organization
                TypeReference.of(PlatformDO.class),
                TypeReference.of(TenantDO.class),
                TypeReference.of(UserDO.class),
                TypeReference.of(UserGroupDO.class),
                TypeReference.of(UserGroupRelDO.class),
                // permission.front
                TypeReference.of(AppDO.class),
                TypeReference.of(PermissionDO.class),
                TypeReference.of(PermissionGroupDO.class),
                TypeReference.of(PermissionItemDO.class),
                // permission.rbac
                TypeReference.of(RbacPermissionAssignDO.class),
                // permission.tbac
                TypeReference.of(TbacPermissionAssignDO.class),
                TypeReference.of(SecurityContainerDO.class)
        );
        hints.reflection().registerTypes(
                references, builder -> builder.withMembers(MemberCategory.values())
        );
    }
}
