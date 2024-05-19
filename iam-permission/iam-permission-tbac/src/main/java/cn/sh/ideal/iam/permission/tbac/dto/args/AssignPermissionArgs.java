package cn.sh.ideal.iam.permission.tbac.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/17
 */
@Getter
@Setter
public class AssignPermissionArgs {
    /**
     * 安全容器ID
     *
     * @required
     */
    @Nullable
    private Long containerId = null;

    /**
     * 用户组ID
     *
     * @required
     */
    @Nullable
    private Long userGroupId = null;

    /**
     * 权限ID列表
     */
    @Nullable
    private Set<Long> permissionIds = null;

    /** 是否授权, 默认是 */
    @Nullable
    private Boolean assign = null;

    /** 是否可继承, 默认否 */
    @Nullable
    private Boolean inheritable = null;

    /** 是否启用双因素认证, 默认否 */
    @Nullable
    private Boolean mfa = null;
}
