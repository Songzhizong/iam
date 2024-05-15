package cn.sh.ideal.iam.organization.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Getter
@Setter
public class CreateUserArgs {

    /** 所属安全容器ID */
    @Nullable
    private Long containerId;

    /**
     * 用户姓名
     *
     * @required
     */
    @Nullable
    private String name;

    /**
     * 用户账号
     */
    @Nullable
    private String account;

    /**
     * 手机号
     */
    @Nullable
    private String phone;

    /**
     * 邮箱
     */
    @Nullable
    private String email;

    /**
     * 语言
     */
    @Nullable
    private String language;

    /**
     * 用户组ID列表
     *
     * <li>为null代表不改表</li>
     * <li>空列表代表清空</li>
     * <li>非空列表代表覆盖</li>
     */
    @Nullable
    private Set<Long> userGroupIds = null;
}
