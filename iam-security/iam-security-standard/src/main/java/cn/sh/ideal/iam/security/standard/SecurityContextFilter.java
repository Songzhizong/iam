package cn.sh.ideal.iam.security.standard;

import cn.idealio.framework.exception.UnauthorizedException;
import cn.idealio.framework.spring.matcher.MethodPathMatcher;
import cn.sh.ideal.iam.security.adapter.AuthenticationProvider;
import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.SecurityContext;
import cn.sh.ideal.iam.security.api.SecurityContextHolder;
import cn.sh.ideal.iam.security.core.configure.SecurityProperties;
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
    private final MethodPathMatcher permitMatcher;
    private final SecurityProperties securityProperties;
    private final AuthenticationProvider authenticationProvider;

    public SecurityContextFilter(@Nonnull SecurityProperties securityProperties,
                                 @Nonnull AuthenticationProvider authenticationProvider) {
        this.securityProperties = securityProperties;
        this.authenticationProvider = authenticationProvider;
        Set<String> permitMatchers = securityProperties.getPermitMatchers();
        this.permitMatcher = MethodPathMatcher.create(permitMatchers);
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
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        if (permitMatcher.matches(method, requestURI)) {
            log.debug("放行请求: {} {}", method, requestURI);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        Authentication authenticate = authenticationProvider.authenticate(request);
        if (authenticate == null) {
            if (!securityProperties.isRequire()) {
                log.info("未认证, 但根据配置放行请求: {} {}", method, requestURI);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            log.info("未认证, 拒绝请求: {} {}", method, requestURI);
            throw new UnauthorizedException();
        }

        SecurityContext securityContext = new SecurityContextImpl(authenticate);
        SecurityContextHolder.setContext(securityContext);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            SecurityContextHolder.clear();
        }
    }
}
