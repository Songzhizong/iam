package cn.sh.ideal.iam.permission.rbac.application;

import cn.sh.ideal.iam.security.api.AccessibleTenant;

import javax.annotation.Nonnull;
import java.util.SequencedCollection;

/**
 * @author 宋志宗 on 2024/6/1
 */
public interface RbacHandler {

    boolean hasAuthority(@Nonnull Long userId, @Nonnull Long tenantId, @Nonnull String authority);

    boolean hasApiPermission(@Nonnull Long userId, @Nonnull Long tenantId,
                             @Nonnull String method, @Nonnull String path);

    @Nonnull
    SequencedCollection<AccessibleTenant> accessibleTenants(@Nonnull Long userId);
}
