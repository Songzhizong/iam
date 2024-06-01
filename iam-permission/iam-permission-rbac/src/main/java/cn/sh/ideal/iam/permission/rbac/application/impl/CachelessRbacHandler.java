package cn.sh.ideal.iam.permission.rbac.application.impl;

import cn.sh.ideal.iam.permission.rbac.application.RbacHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/1
 */
@Component
public class CachelessRbacHandler implements RbacHandler {

    @Override
    public boolean hasAuthority(long userId,
                                long tenantId,
                                @Nonnull String authority) {
        return false;
    }

    @Override
    public boolean hasApiPermission(long userId, long tenantId,
                                    @Nonnull String method, @Nonnull String path) {
        return false;
    }
}
