package cn.sh.ideal.iam.infrastructure.encryption;

import cn.idealio.framework.exception.InternalServerException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.crypto.AES;
import cn.sh.ideal.iam.infrastructure.configure.EncryptionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Component
public class AesEncryptionProvider implements EncryptionProvider {
    public static final String TYPE = "aes";
    private static final String PREFIX = "$ae$";
    private static final int PREFIX_LENGTH = PREFIX.length();
    private final byte[] secret;

    public AesEncryptionProvider(EncryptionProperties encryptionProperties) {
        String aesSec = encryptionProperties.getAesSec();
        byte[] bytes = aesSec.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        if (length == 32) {
            this.secret = bytes;
        } else {
            byte[] secret = new byte[32];
            for (int i = 0; i < 32; i++) {
                secret[i] = i < length ? bytes[i] : 0;
            }
            this.secret = secret;
        }
    }

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
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] encrypt = AES.encrypt(bytes, secret);
        String base64 = Base64.getEncoder().encodeToString(encrypt);
        return PREFIX + base64;
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
        byte[] decode = Base64.getDecoder().decode(encrypted);
        byte[] decrypt = AES.decrypt(decode, secret);
        return new String(decrypt, StandardCharsets.UTF_8);
    }
}
