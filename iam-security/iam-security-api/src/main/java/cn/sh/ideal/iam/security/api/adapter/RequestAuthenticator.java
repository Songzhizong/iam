package cn.sh.ideal.iam.security.api.adapter;

import cn.sh.ideal.iam.security.api.Authentication;
import jakarta.servlet.http.HttpServletRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/16
 */
public interface RequestAuthenticator {

    @Nullable
    Authentication authenticate(@Nonnull HttpServletRequest request);

}
