package cn.sh.ideal.iam.authorization.configure;

import cn.idealio.framework.spring.SpringI18nReader;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/3/5
 */
@Component
public class AuthorizationI18nReader extends SpringI18nReader {

    public AuthorizationI18nReader(@Nonnull LocaleResolver localeResolver,
                                   @Nonnull HttpServletRequest httpServletRequest) {
        super("i18n/iam-authorization", localeResolver, httpServletRequest);
    }
}
