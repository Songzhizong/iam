package cn.sh.ideal.iam.security.standard.configure;

import cn.sh.ideal.iam.security.standard.SecurityInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2023/9/26
 */
@Configuration
@RequiredArgsConstructor
public class SecurityStandardWebMvcConfigurer implements WebMvcConfigurer {
    private final SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor).addPathPatterns("/**");
    }
}

