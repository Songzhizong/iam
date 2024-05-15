package cn.sh.ideal.iam.infrastructure.encryption;

import cn.idealio.framework.exception.InternalServerException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.crypto.AES;
import cn.sh.ideal.iam.infrastructure.configure.EncryptionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AesEncryptionProvider implements EncryptionProvider {
    public static final String TYPE = "aes";
    private static final String PREFIX = "$ae$";
    private static final int PREFIX_LENGTH = PREFIX.length();
    private final EncryptionProperties encryptionProperties;

    @Nonnull
    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean isSupportByEncrypted(@Nonnull String encrypted) {
        return encrypted.startsWith(PREFIX);
    }

    @Nonnull
    @Override
    public String encrypt(@Nullable String text) {
        if (StringUtils.isBlank(text)) {
            return EncryptionProvider.randomEmpty();
        }
        String encrypt = AES.encrypt(text, encryptionProperties.getAesSec());
        return PREFIX + encrypt;
    }

    @Nonnull
    @Override
    public String decrypt(@Nullable String encrypted) {
        if (StringUtils.isBlank(encrypted)) {
            return "";
        }
        if (encrypted.startsWith(PREFIX)) {
            encrypted = encrypted.substring(PREFIX_LENGTH);
        } else {
            throw new InternalServerException("Unsupported encrypted value: " + encrypted);
        }
        return AES.decrypt(encrypted, encryptionProperties.getAesSec());
    }
}
