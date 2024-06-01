package cn.sh.ideal.iam.permission.composite;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantCache;
import cn.sh.ideal.iam.organization.exception.TenantBlockedException;
import cn.sh.ideal.iam.security.api.PermissionValidator;
import cn.sh.ideal.iam.security.api.TenantAccessibility;
import cn.sh.ideal.iam.security.api.adapter.TenantAccessibilityFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantAccessibilityFactoryImpl implements TenantAccessibilityFactory {
    private final TenantCache tenantCache;
    private final IamI18nReader i18nReader;
    private final PermissionValidator permissionValidator;


    @Override
    public TenantAccessibility createTenantAccessibility(long userId) {
        return new TenantAccessibilityImpl(userId, tenantCache, i18nReader, permissionValidator);
    }

    @RequiredArgsConstructor
    public static class TenantAccessibilityImpl implements TenantAccessibility {
        private final long userId;
        private final TenantCache tenantCache;
        private final IamI18nReader i18nReader;
        private final PermissionValidator permissionValidator;

        @Override
        public boolean isAccessible(long tenantId) {
            Tenant tenant = tenantCache.get(tenantId).orElse(null);
            if (tenant == null) {
                log.info("租户不存在: [{}]", tenantId);
                return false;
            }
            if (tenant.isBlocked()) {
                log.info("认证失败, 客户: [{}] 已被冻结", tenantId);
                String message = i18nReader.getMessage("tenant.blocked");
                throw new TenantBlockedException(message);
            }
            return permissionValidator.validateTenantAccess(userId, tenantId);
        }
    }
}
