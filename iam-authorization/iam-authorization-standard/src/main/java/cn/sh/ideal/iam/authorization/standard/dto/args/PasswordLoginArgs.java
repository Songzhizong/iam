package cn.sh.ideal.iam.authorization.standard.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Getter
@Setter
public class PasswordLoginArgs {

    @Nullable
    private String username;

    @Nullable
    private String password;

    @Nullable
    public String rawPassword() {
        return password;
    }
}
