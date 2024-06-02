package cn.sh.ideal.iam.security.standard;

import cn.idealio.framework.exception.ForbiddenException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.security.api.PermissionValidator;
import cn.sh.ideal.iam.security.api.SecurityContext;
import cn.sh.ideal.iam.security.api.SecurityContextHolder;
import cn.sh.ideal.iam.security.api.annotation.HasAuthority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class SecurityInterceptor implements HandlerInterceptor, Ordered {
    @Nullable
    private IamI18nReader i18nReader;

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request,
                             @Nonnull HttpServletResponse response,
                             @Nonnull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        SecurityContext securityContext = SecurityContextHolder.optional().orElse(null);
        if (securityContext == null) {
            return true;
        }
        HasAuthority authority = handlerMethod.getMethodAnnotation(HasAuthority.class);
        if (authority != null) {
            String value = authority.value();
            PermissionValidator permissionValidator = securityContext.permissionValidator();
            boolean hasAuthority = permissionValidator.hasAuthority(value);
            if (!hasAuthority) {
                Long userId = securityContext.authentication().userId();
                log.warn("用户 [{}] 没有此项权限: [{}]", userId, value);
                String message;
                if (i18nReader == null) {
                    message = "没有此项权限: " + value;
                } else {
                    message = i18nReader.getMessage1("security.authority.forbidden", value);
                }
                throw new ForbiddenException(message);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request,
                                @Nonnull HttpServletResponse response,
                                @Nonnull Object handler,
                                @Nullable Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
