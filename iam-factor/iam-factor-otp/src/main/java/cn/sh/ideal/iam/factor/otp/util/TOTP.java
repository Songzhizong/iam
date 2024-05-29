package cn.sh.ideal.iam.factor.otp.util;

import cn.sh.ideal.iam.factor.otp.util.googleauth.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Time-based One-Time Password
 *
 * @author 宋志宗 on 2022/12/4
 */
@SuppressWarnings("ClassCanBeRecord")
public class TOTP {
    /**
     * The format string to generate the Google Chart HTTP API call.
     */
//    private static final String TOTP_URI_FORMAT =
//            "https://api.qrserver.com/v1/create-qr-code/?format=png&size=200x200&ecc=L&margin=6&data=";
    private static final GoogleAuthenticator AUTHENTICATOR = new GoogleAuthenticator(
            new GoogleAuthenticatorConfig
                    .GoogleAuthenticatorConfigBuilder()
                    .setHmacHashFunction(HmacHashFunction.HmacSHA1)
                    .setSecretBits(160)
                    .build()
    );

    @Nonnull
    private final String secret;
    @Nonnull
    private final String otpAuthTotpURL;

    public TOTP(@Nonnull String secret, @Nonnull String otpAuthTotpURL) {
        this.secret = secret;
        this.otpAuthTotpURL = otpAuthTotpURL;
    }

    /**
     * 生成TOTP
     *
     * @param issuer      发行人名称,不能包含冒号(:)字符,可为null
     * @param accountName 用户的账号名称
     * @return totp
     */
    @Nonnull
    public static TOTP generate(@Nullable String issuer, @Nonnull String accountName) {
        GoogleAuthenticatorKey credentials = AUTHENTICATOR.createCredentials();
        String secret = credentials.getKey();
        String otpAuthTotpURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(issuer, accountName, credentials);
        return new TOTP(secret, otpAuthTotpURL);
    }

    public static boolean authenticate(@Nonnull String secret, int code) {
        return AUTHENTICATOR.authorize(secret, code);
    }

    @Nonnull
    public String getSecret() {
        return secret;
    }

    @Nonnull
    public String getOtpAuthTotpURL() {
        return otpAuthTotpURL;
    }

//    @Nonnull
//    public String genQRCodeUrl() {
//        return TOTP_URI_FORMAT + URLEncoder.encode(otpAuthTotpURL, StandardCharsets.UTF_8);
//    }
}
