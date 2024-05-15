package cn.sh.ideal.iam.infrastructure.configure;

import cn.sh.ideal.iam.infrastructure.encryption.AesEncryptionProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2023/12/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ideal-iam.encryption")
public class EncryptionProperties {

    /** 默认加密类型 */
    @Nonnull
    private String defaultEncryptionType = AesEncryptionProvider.TYPE;

    @Nonnull
    private String aesSec = "JQWZc-H4YJd7sY-Rd4TRWb48uTWVMqY3";
}
