package cn.sh.ideal.iam.permission.composite;

import cn.idealio.framework.exception.ForbiddenException;
import cn.sh.ideal.iam.common.util.RequestUtils;
import cn.sh.ideal.iam.permission.core.PermissionModel;
import cn.sh.ideal.iam.permission.front.domain.model.App;
import cn.sh.ideal.iam.permission.front.domain.model.AppCache;
import cn.sh.ideal.iam.permission.rbac.application.RbacSecurityService;
import cn.sh.ideal.iam.permission.tbac.application.TbacSecurityService;
import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.PermissionValidator;
import cn.sh.ideal.iam.security.api.PermitAllPermissionValidator;
import cn.sh.ideal.iam.security.api.adapter.PermissionValidatorFactory;
import cn.sh.ideal.iam.security.api.adapter.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/31
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionValidatorFactoryImpl implements PermissionValidatorFactory {
    private final AppCache appCache;
    private final RbacSecurityService rbacSecurityService;
    private final TbacSecurityService tbacSecurityService;
    private final CompositeSecurityService compositeSecurityService;

    @Nonnull
    @Override
    public PermissionValidator createPermissionValidator(@Nonnull Authentication authentication,
                                                         @Nonnull HttpServletRequest httpServletRequest) {
        PermissionModel permissionModel = null;
        Long appId = RequestUtils.getAppId(httpServletRequest);
        if (appId != null) {
            App app = appCache.findById(appId).orElse(null);
            if (app != null) {
                if (!app.isApiAuthenticateEnabled()) {
                    return PermitAllPermissionValidator.getInstance();
                }
                permissionModel = app.getPermissionModel();
            }
        }

        Long tenantId = RequestUtils.getTenantId(httpServletRequest);
        if (tenantId == null) {
            tenantId = authentication.tenantId();
        }

        Long userId = authentication.userId();
        if (permissionModel == null || permissionModel == PermissionModel.NONE) {
            return new CompositePermissionValidator(userId, tenantId, compositeSecurityService);
        }
        if (permissionModel == PermissionModel.TBAC) {
            return new TbacPermissionValidator(userId, tenantId, tbacSecurityService);
        }
        if (permissionModel == PermissionModel.RBAC) {
            return new RbacPermissionValidator(userId, tenantId, rbacSecurityService);
        }
        log.error("不支持的权限模型: {}", permissionModel);
        throw new ForbiddenException("不支持的权限模型: " + permissionModel);
    }

    @RequiredArgsConstructor
    public static class RbacPermissionValidator implements PermissionValidator {
        @Nonnull
        private final Long userId;
        @Nonnull
        private final Long tenantId;
        private final SecurityService securityService;


        @Override
        public boolean hasAuthority(@Nonnull String authority) {
            return securityService.hasAuthority(userId, tenantId, authority);
        }

        @Override
        public boolean hasApiPermission(@Nonnull String method, @Nonnull String path) {
            return securityService.hasApiPermission(userId, tenantId, method, path);
        }
    }

    @RequiredArgsConstructor
    public static class TbacPermissionValidator implements PermissionValidator {
        @Nonnull
        private final Long userId;
        @Nonnull
        private final Long tenantId;
        private final SecurityService securityService;


        @Override
        public boolean hasAuthority(@Nonnull String authority) {
            return securityService.hasAuthority(userId, tenantId, authority);
        }

        @Override
        public boolean hasApiPermission(@Nonnull String method, @Nonnull String path) {
            return securityService.hasApiPermission(userId, tenantId, method, path);
        }
    }

    @RequiredArgsConstructor
    public static class CompositePermissionValidator implements PermissionValidator {
        @Nonnull
        private final Long userId;
        @Nonnull
        private final Long tenantId;
        private final SecurityService securityService;


        @Override
        public boolean hasAuthority(@Nonnull String authority) {
            return securityService.hasAuthority(userId, tenantId, authority);
        }

        @Override
        public boolean hasApiPermission(@Nonnull String method, @Nonnull String path) {
            return securityService.hasApiPermission(userId, tenantId, method, path);
        }
    }
}
