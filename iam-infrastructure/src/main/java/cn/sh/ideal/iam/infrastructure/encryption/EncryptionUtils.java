package cn.sh.ideal.iam.infrastructure.encryption;

import cn.idealio.framework.exception.InternalServerException;
import cn.idealio.framework.lang.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
public class EncryptionUtils {
    @Getter
    @Setter
    private static EncryptionProvider defaultProvider;
    @Setter
    private static List<EncryptionProvider> providers;


    @Nonnull
    public static String encrypt(@Nullable String text) {
        if (StringUtils.isBlank(text)) {
            return EncryptionProvider.randomEmpty();
        }
        return defaultProvider.encrypt(text);
    }

    @Nonnull
    public static String decrypt(@Nullable String encrypted) {
        if (StringUtils.isEmpty(encrypted)) {
            return "";
        }
        if (EncryptionProvider.isEmpty(encrypted)) {
            return "";
        }
        for (EncryptionProvider provider : providers) {
            if (provider.isSupportByEncrypted(encrypted)) {
                return provider.decrypt(encrypted);
            }
        }
        log.error("不支持的加密类型: {}", encrypted);
        throw new InternalServerException("Unsupported encryption type");
    }
}
