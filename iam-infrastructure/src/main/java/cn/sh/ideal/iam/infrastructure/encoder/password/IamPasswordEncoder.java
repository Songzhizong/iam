package cn.sh.ideal.iam.infrastructure.encoder.password;

import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.crypto.HmacSHA1;
import cn.idealio.framework.util.crypto.MD5;
import cn.idealio.framework.util.crypto.SHA256;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 宋志宗 on 2021/11/20
 */
@Component
public class IamPasswordEncoder implements PasswordEncoder {
    private static final String PASSWORD_SALT = "7aRw.fXAF*ohWgdJRH!aZyE7LEhWZ6Nz";
    private static final IamPasswordEncoder INSTANCE = new IamPasswordEncoder();

    private IamPasswordEncoder() {
    }

    @Nonnull
    public static IamPasswordEncoder instance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public String encode(@Nonnull CharSequence rawPassword) {
        return encryptPwd(rawPassword);
    }

    @Override
    public boolean matches(@Nonnull CharSequence rawPassword, @Nonnull String encodedPassword) {
        return encryptPwd(rawPassword.toString()).equals(encodedPassword);
    }

    @Nonnull
    private String encryptPwd(@Nonnull CharSequence rawPassword) {
        String rawPasswordStr = rawPassword.toString();
        Base64.Encoder base64Encoder = Base64.getEncoder();
        String en1 = SHA256.encode(rawPasswordStr + PASSWORD_SALT);
        String en2 = base64Encoder.encodeToString(HmacSHA1.encode(PASSWORD_SALT, rawPasswordStr));
        String en3 = MD5.encode(en1 + PASSWORD_SALT + en2 + rawPasswordStr);
        String en3Base64 = base64Encoder.encodeToString(en3.getBytes(StandardCharsets.US_ASCII));
        String en1Base64 = base64Encoder.encodeToString(en1.getBytes(StandardCharsets.US_ASCII));
        String s = "$iam$" + en3Base64 + en1Base64 + en2;
        return StringUtils.replace(s, "=", "");
    }
}
