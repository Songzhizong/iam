package cn.sh.ideal.iam.organization.configure;

import cn.idealio.framework.spring.SpringI18nReader;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/3/5
 */
@Component
public class OrganizationI18nReader extends SpringI18nReader {

    public OrganizationI18nReader(@Nonnull LocaleResolver localeResolver,
                                  @Nonnull HttpServletRequest httpServletRequest) {
        super("i18n/iam-organization", localeResolver, httpServletRequest);
    }
}
