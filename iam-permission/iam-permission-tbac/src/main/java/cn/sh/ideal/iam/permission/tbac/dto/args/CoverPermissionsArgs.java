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
public class CoverPermissionsArgs {
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
     * 权限分配信息, 为空代表清空所有权限
     */
    @Nullable
    private Set<AssignPermissionArgs> permissions = null;
}
