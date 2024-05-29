package cn.sh.ideal.iam.factor.otp.port.web;

import cn.idealio.framework.transmission.Result;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.factor.otp.application.UserTotpService;
import cn.sh.ideal.iam.factor.otp.util.TOTP;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/29
 * @ignore
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/test")
public class TestTotpController {
    private final UserTotpService userTotpService;

    /** 生成otp */
    @GetMapping("/generate")
    public Result<String> generate(@Nullable Long userId) {
        Asserts.nonnull(userId, "userId");
        TOTP totp = userTotpService.generate(userId);
        return Result.success(totp.getOtpAuthTotpURL());
    }

    /** 确认otp */
    @PostMapping("/confirmation")
    public Result<Void> confirmation(@Nullable Long userId,
                                     @Nullable Integer code) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(code, "code");
        userTotpService.confirmation(userId, code);
        return Result.success();
    }

    /** 删除otp */
    @DeleteMapping("/delete")
    public Result<String> delete(@Nullable Long userId) {
        Asserts.nonnull(userId, "userId");
        userTotpService.delete(userId);
        return Result.success();
    }

    /** 验证otp */
    @PostMapping("/authenticate")
    public Result<Void> authenticate(@Nullable Long userId,
                                     @Nullable Integer code) {
        Asserts.nonnull(userId, "userId");
        Asserts.nonnull(code, "code");
        userTotpService.authenticate(userId, code);
        return Result.success();
    }
}
