package cn.sh.ideal.iam.permission.composite;

import cn.idealio.framework.concurrent.Asyncs;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.domain.model.UserCache;
import cn.sh.ideal.iam.security.api.AccessibleTenant;
import cn.sh.ideal.iam.security.api.adapter.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
@SuppressWarnings("DuplicatedCode")
public class CompositeSecurityService implements SecurityService {
    private final UserCache userCache;
    private final List<SecurityService> securityServices;

    @Override
    public boolean hasAuthority(@Nonnull Long userId,
                                @Nonnull Long tenantId,
                                @Nonnull String authority) {
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < securityServices.size() - 1; i++) {
            SecurityService securityService = securityServices.get(i);
            Future<Boolean> future = Asyncs.submitVirtual(() ->
                    securityService.hasAuthority(userId, tenantId, authority)
            );
            futures.add(future);
        }
        SecurityService last = securityServices.getLast();
        boolean hasAuthority = last.hasAuthority(userId, tenantId, authority);
        if (hasAuthority) {
            return true;
        }
        for (Future<Boolean> future : futures) {
            Boolean await = Asyncs.await(future);
            if (Boolean.TRUE.equals(await)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasApiPermission(@Nonnull Long userId, @Nonnull Long tenantId,
                                    @Nonnull String method, @Nonnull String path) {
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < securityServices.size() - 1; i++) {
            SecurityService securityService = securityServices.get(i);
            Future<Boolean> future = Asyncs.submitVirtual(() ->
                    securityService.hasApiPermission(userId, tenantId, method, path)
            );
            futures.add(future);
        }
        SecurityService last = securityServices.getLast();
        boolean hasApiPermission = last.hasApiPermission(userId, tenantId, method, path);
        if (hasApiPermission) {
            return true;
        }
        for (Future<Boolean> future : futures) {
            Boolean await = Asyncs.await(future);
            if (Boolean.TRUE.equals(await)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTenantAccessible(@Nonnull Long userId, @Nonnull Long tenantId) {
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < securityServices.size() - 1; i++) {
            SecurityService securityService = securityServices.get(i);
            Future<Boolean> future = Asyncs.submitVirtual(() ->
                    securityService.isTenantAccessible(userId, tenantId)
            );
            futures.add(future);
        }
        SecurityService last = securityServices.getLast();
        boolean validateTenantAccess = last.isTenantAccessible(userId, tenantId);
        if (validateTenantAccess) {
            return true;
        }
        for (Future<Boolean> future : futures) {
            Boolean await = Asyncs.await(future);
            if (Boolean.TRUE.equals(await)) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public SequencedCollection<AccessibleTenant> accessibleTenants(@Nonnull Long userId) {
        List<Future<Collection<AccessibleTenant>>> futures = new ArrayList<>();
        for (int i = 0; i < securityServices.size() - 1; i++) {
            SecurityService securityService = securityServices.get(i);
            Future<Collection<AccessibleTenant>> future = Asyncs.submitVirtual(() ->
                    securityService.accessibleTenants(userId)
            );
            futures.add(future);
        }
        SecurityService last = securityServices.getLast();
        Collection<AccessibleTenant> accessibleTenants = last.accessibleTenants(userId);

        Set<AccessibleTenant> tenants = new HashSet<>(accessibleTenants);

        for (Future<Collection<AccessibleTenant>> future : futures) {
            Collection<AccessibleTenant> await = Asyncs.await(future);
            tenants.addAll(await);
        }
        if (tenants.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<AccessibleTenant> sorted = tenants.stream()
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        Long userTenantId = userCache.get(userId).map(User::getTenantId).orElse(null);
        if (userTenantId == null) {
            return sorted;
        }
        // 将当前用户的租户排在第一位
        AccessibleTenant first = null;
        for (AccessibleTenant accessibleTenant : sorted) {
            if (accessibleTenant.getId().equals(userTenantId)) {
                first = accessibleTenant;
                break;
            }
        }
        if (first != null) {
            sorted.addFirst(first);
        }
        return sorted;
    }
}
