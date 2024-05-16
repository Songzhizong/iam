package cn.sh.ideal.iam.permission.front.configure;

import cn.idealio.framework.spring.SpringI18nReader;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/3/5
 */
@Component
public class FrontI18nReader extends SpringI18nReader {

    public FrontI18nReader(@Nonnull LocaleResolver localeResolver,
                           @Nonnull HttpServletRequest httpServletRequest) {
        super("i18n/iam-tbac", localeResolver, httpServletRequest);
    }
}
