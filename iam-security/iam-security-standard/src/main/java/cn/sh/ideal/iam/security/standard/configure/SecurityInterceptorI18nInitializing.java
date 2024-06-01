package cn.sh.ideal.iam.security.standard.configure;

import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.security.standard.SecurityInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 为SecurityInterceptor初始化IamI18nReader, 因为直接注入会导致循环依赖
 *
 * @author 宋志宗 on 2024/5/31
 */
@Component
@RequiredArgsConstructor
public class SecurityInterceptorI18nInitializing implements InitializingBean {
    private final IamI18nReader i18nReader;
    private final SecurityInterceptor securityInterceptor;

    @Override
    public void afterPropertiesSet() {
        this.securityInterceptor.setI18nReader(this.i18nReader);
    }
}
