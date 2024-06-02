package cn.sh.ideal.iam.permission.composite;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantCache;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.domain.model.UserCache;
import cn.sh.ideal.iam.organization.exception.TenantBlockedException;
import cn.sh.ideal.iam.security.api.AccessibleTenant;
import cn.sh.ideal.iam.security.api.TenantAccessibility;
import cn.sh.ideal.iam.security.api.adapter.SecurityService;
import cn.sh.ideal.iam.security.api.adapter.TenantAccessibilityFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.SequencedCollection;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantAccessibilityFactoryImpl implements TenantAccessibilityFactory {
    private final UserCache userCache;
    private final TenantCache tenantCache;
    private final IamI18nReader i18nReader;
    private final SecurityService securityService;


    @Override
    public TenantAccessibility createTenantAccessibility(@Nonnull Long userId) {
        return new TenantAccessibilityImpl(userId, userCache, tenantCache, i18nReader, securityService);
    }

    @RequiredArgsConstructor
    public static class TenantAccessibilityImpl implements TenantAccessibility {
        @Nonnull
        private final Long userId;
        private final UserCache userCache;
        private final TenantCache tenantCache;
        private final IamI18nReader i18nReader;
        private final SecurityService securityService;

        @Override
        public boolean isAccessible(@Nonnull Long tenantId) {
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
            User user = userCache.require(userId);
            if (user.getTenantId().equals(tenant.getId())) {
                return true;
            }
            return securityService.isTenantAccessible(userId, tenantId);
        }

        @Nonnull
        @Override
        public SequencedCollection<AccessibleTenant> accessibleTenants() {
            return securityService.accessibleTenants(userId);
        }
    }
}
