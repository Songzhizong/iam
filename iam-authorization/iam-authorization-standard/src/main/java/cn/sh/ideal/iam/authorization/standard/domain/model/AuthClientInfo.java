package cn.sh.ideal.iam.authorization.standard.domain.model;

import cn.sh.ideal.iam.common.constant.Terminal;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Getter
@Setter
public class AuthClientInfo {

    @Nonnull
    private Long id = -1L;

    private String platform = "";

    private String name = "";

    private String note = "";

    private Terminal terminal = Terminal.WEB;

    private String token = "";
}
