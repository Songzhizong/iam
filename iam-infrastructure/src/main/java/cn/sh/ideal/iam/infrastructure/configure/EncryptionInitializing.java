package cn.sh.ideal.iam.infrastructure.configure;

import cn.sh.ideal.iam.infrastructure.encryption.EncryptionProvider;
import cn.sh.ideal.iam.infrastructure.encryption.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionInitializing implements InitializingBean {
    private final EncryptionProperties encryptionProperties;
    private final List<EncryptionProvider> encryptionProviders;

    @Override
    public void afterPropertiesSet() {
        String defaultEncryptionType = encryptionProperties.getDefaultEncryptionType();
        for (EncryptionProvider encryptionProvider : encryptionProviders) {
            if (defaultEncryptionType.equals(encryptionProvider.getType())) {
                EncryptionUtils.setDefaultProvider(encryptionProvider);
                log.info("EncryptionUtils设置默认加密算法为 {}", defaultEncryptionType);
                break;
            }
        }
        if (EncryptionUtils.getDefaultProvider() == null) {
            log.error("EncryptionUtils未设置默认加密算法");
            System.exit(1);
        }
        EncryptionUtils.setProviders(encryptionProviders);
    }
}
