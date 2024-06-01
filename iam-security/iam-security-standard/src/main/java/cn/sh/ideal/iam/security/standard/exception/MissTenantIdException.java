package cn.sh.ideal.iam.security.standard.exception;

import cn.idealio.framework.exception.VisibleException;
import cn.sh.ideal.iam.common.constant.IamGuides;

import javax.annotation.Nonnull;

/**
 * 请求未携带租户ID
 *
 * @author 宋志宗 on 2022/8/16
 */
public class MissTenantIdException extends VisibleException {

    public MissTenantIdException(@Nonnull String message) {
        super(403, IamGuides.MESSING_TENANT_ID, message);
    }
}
