package cn.sh.ideal.iam.security.standard.exception;

import cn.idealio.framework.exception.VisibleException;
import cn.sh.ideal.iam.common.constant.IamGuides;

import javax.annotation.Nonnull;

/**
 * 没有指定的租户访问权限
 *
 * @author 宋志宗 on 2022/8/16
 */
public class TenantAccessDeniedException extends VisibleException {

    public TenantAccessDeniedException(@Nonnull String message) {
        super(403, IamGuides.TENANT_FORBIDDEN, message);
    }
}
