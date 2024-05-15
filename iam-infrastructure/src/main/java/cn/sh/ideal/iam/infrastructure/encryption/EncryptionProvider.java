package cn.sh.ideal.iam.infrastructure.encryption;

import cn.idealio.framework.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface EncryptionProvider {
    String EMPTY_PREFIX = "$empty$";

    @Nonnull
    String getType();

    boolean isSupportByEncrypted(@Nonnull String encrypted);

    @Nonnull
    String encrypt(@Nullable String text);

    @Nonnull
    String decrypt(@Nullable String encrypted);

    @Nonnull
    static String randomEmpty() {
        return EMPTY_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }

    static boolean isEmpty(@Nullable String encrypted) {
        if (encrypted == null) {
            return true;
        }
        return StringUtils.startsWith(encrypted, EMPTY_PREFIX);
    }
}
