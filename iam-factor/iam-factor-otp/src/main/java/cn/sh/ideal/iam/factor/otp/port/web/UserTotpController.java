package cn.sh.ideal.iam.factor.otp.port.web;

import cn.sh.ideal.iam.factor.otp.application.UserTotpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 手机令牌管理
 *
 * @author 宋志宗 on 2024/5/29
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam")
public class UserTotpController {
    private final UserTotpService userTotpService;

}
