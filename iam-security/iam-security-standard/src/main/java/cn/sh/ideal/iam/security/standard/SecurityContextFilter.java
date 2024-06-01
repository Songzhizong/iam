package cn.sh.ideal.iam.security.standard;

import cn.idealio.framework.exception.UnauthorizedException;
import cn.idealio.framework.spring.matcher.MethodPathMatcher;
import cn.sh.ideal.iam.common.util.RequestUtils;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.security.api.*;
import cn.sh.ideal.iam.security.api.adapter.AuthorityValidatorFactory;
import cn.sh.ideal.iam.security.api.adapter.RequestAuthenticator;
import cn.sh.ideal.iam.security.api.adapter.TenantAccessibilityFactory;
import cn.sh.ideal.iam.security.core.configure.SecurityProperties;
import cn.sh.ideal.iam.security.standard.exception.MissTenantIdException;
import cn.sh.ideal.iam.security.standard.exception.TenantAccessDeniedException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Component
public class SecurityContextFilter implements Filter, Ordered {
    private final IamI18nReader i18nReader;
    private final MethodPathMatcher permitMatcher;
    private final MethodPathMatcher tenantAccessMatcher;
    private final SecurityProperties securityProperties;
    private final RequestAuthenticator requestAuthenticator;
    private final AuthorityValidatorFactory authorityValidatorFactory;
    private final TenantAccessibilityFactory tenantAccessibilityFactory;

    public SecurityContextFilter(@Nonnull IamI18nReader i18nReader,
                                 @Nonnull SecurityProperties securityProperties,
                                 @Nonnull RequestAuthenticator requestAuthenticator,
                                 @Nonnull AuthorityValidatorFactory authorityValidatorFactory,
                                 @Nonnull TenantAccessibilityFactory tenantAccessibilityFactory) {
        this.i18nReader = i18nReader;
        this.securityProperties = securityProperties;
        this.requestAuthenticator = requestAuthenticator;
        this.authorityValidatorFactory = authorityValidatorFactory;
        this.tenantAccessibilityFactory = tenantAccessibilityFactory;
        Set<String> permitMatchers = securityProperties.getPermitMatchers();
        this.permitMatcher = MethodPathMatcher.create(permitMatchers);
        Set<String> tenantAccessMatchers = securityProperties.getTenantAccessMatchers();
        this.tenantAccessMatcher = MethodPathMatcher.create(tenantAccessMatchers);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void doFilter(@Nonnull ServletRequest servletRequest,
                         @Nonnull ServletResponse servletResponse,
                         @Nonnull FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        try {
            String method = request.getMethod();
            String requestURI = request.getRequestURI();
            if (permitMatcher.matches(method, requestURI)) {
                log.debug("放行请求: {} {}", method, requestURI);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            // 登录认证
            Authentication authentication = requestAuthenticator.authenticate(request);
            if (authentication == null) {
                if (!securityProperties.isRequire()) {
                    log.info("未认证, 但根据配置放行请求: {} {}", method, requestURI);
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
                log.info("未认证, 拒绝请求: {} {}", method, requestURI);
                throw new UnauthorizedException();
            }

            // 租户可访问性
            long userId = authentication.userId();
            TenantAccessibility tenantAccessibility =
                    tenantAccessibilityFactory.createTenantAccessibility(userId);

            // 验证租户访问权限
            if (!tenantAccessMatcher.matches(method, requestURI)) {
                Long requestTenantId = RequestUtils.getTenantId(request);
                if (requestTenantId == null) {
                    log.info("请求未携带租户信息, 拒绝请求: {} {}", method, requestURI);
                    String message = i18nReader.getMessage("tenant.id.null");
                    throw new MissTenantIdException(message);
                }
                boolean accessible = tenantAccessibility.isAccessible(requestTenantId);
                if (!accessible) {
                    log.info("用户无权访问租户: {} {}", requestTenantId, requestURI);
                    String message = i18nReader
                            .getMessage1("tenant.access.denied", requestTenantId);
                    throw new TenantAccessDeniedException(message);
                }
            }

            // 权限验证器
            AuthorityValidator authorityValidator =
                    authorityValidatorFactory.createAuthorityValidator(authentication, request);

            SecurityContext securityContext = new SecurityContextImpl(
                    authentication, authorityValidator, tenantAccessibility);
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            SecurityContextHolder.clear();
        }
    }
}
