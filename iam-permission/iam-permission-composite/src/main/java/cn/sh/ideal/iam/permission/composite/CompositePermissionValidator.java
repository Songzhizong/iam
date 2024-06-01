package cn.sh.ideal.iam.permission.composite;

import cn.idealio.framework.concurrent.Asyncs;
import cn.sh.ideal.iam.security.api.PermissionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
@SuppressWarnings("DuplicatedCode")
public class CompositePermissionValidator implements PermissionValidator {
    private final List<PermissionValidator> permissionValidators;

    @Override
    public boolean hasAuthority(@Nonnull Long userId,
                                @Nonnull Long tenantId,
                                @Nonnull String authority) {
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < permissionValidators.size() - 1; i++) {
            PermissionValidator permissionValidator = permissionValidators.get(i);
            Future<Boolean> future = Asyncs.submitVirtual(() ->
                    permissionValidator.hasAuthority(userId, tenantId, authority)
            );
            futures.add(future);
        }
        PermissionValidator last = permissionValidators.getLast();
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
    public boolean hasApiPermission(long userId, long tenantId,
                                    @Nonnull String method, @Nonnull String path) {
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < permissionValidators.size() - 1; i++) {
            PermissionValidator permissionValidator = permissionValidators.get(i);
            Future<Boolean> future = Asyncs.submitVirtual(() ->
                    permissionValidator.hasApiPermission(userId, tenantId, method, path)
            );
            futures.add(future);
        }
        PermissionValidator last = permissionValidators.getLast();
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
    public boolean validateTenantAccess(long userId, @Nonnull Long tenantId) {
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < permissionValidators.size() - 1; i++) {
            PermissionValidator permissionValidator = permissionValidators.get(i);
            Future<Boolean> future = Asyncs.submitVirtual(() ->
                    permissionValidator.validateTenantAccess(userId, tenantId)
            );
            futures.add(future);
        }
        PermissionValidator last = permissionValidators.getLast();
        boolean validateTenantAccess = last.validateTenantAccess(userId, tenantId);
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
}
