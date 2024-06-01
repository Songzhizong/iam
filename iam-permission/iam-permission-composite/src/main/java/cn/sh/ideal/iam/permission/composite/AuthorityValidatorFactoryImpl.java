package cn.sh.ideal.iam.permission.composite;

import cn.sh.ideal.iam.common.util.RequestUtils;
import cn.sh.ideal.iam.security.api.Authentication;
import cn.sh.ideal.iam.security.api.AuthorityValidator;
import cn.sh.ideal.iam.security.api.adapter.AuthorityValidatorFactory;
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
public class AuthorityValidatorFactoryImpl implements AuthorityValidatorFactory {
    private final CompositePermissionValidator compositePermissionValidator;

    @Nonnull
    @Override
    public AuthorityValidator createAuthorityValidator(@Nonnull Authentication authentication,
                                                       @Nonnull HttpServletRequest httpServletRequest) {
        Long tenantId = RequestUtils.getTenantId(httpServletRequest);
        if (tenantId == null) {
            tenantId = authentication.tenantId();
        }

        long userId = authentication.userId();
        return new CompositeAuthorityValidator(userId, tenantId, compositePermissionValidator);
    }

    @RequiredArgsConstructor
    public static class CompositeAuthorityValidator implements AuthorityValidator {
        private final long userId;
        private final long tenantId;
        private final CompositePermissionValidator compositePermissionValidator;


        @Override
        public boolean hasAuthority(@Nonnull String authority) {
            return compositePermissionValidator.hasAuthority(userId, tenantId, authority);
        }

        @Override
        public boolean hasApiPermission(@Nonnull String method, @Nonnull String path) {
            return compositePermissionValidator.hasApiPermission(userId, tenantId, method, path);
        }
    }
}
