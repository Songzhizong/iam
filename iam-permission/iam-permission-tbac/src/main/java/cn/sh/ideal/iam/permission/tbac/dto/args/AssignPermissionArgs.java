package cn.sh.ideal.iam.permission.tbac.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author 宋志宗 on 2024/5/17
 */
@Getter
@Setter
public class AssignPermissionArgs {

    /**
     * 权限ID
     *
     * @required
     */
    @Nullable
    private Long permissionId = null;

    /** 是否授权, 默认是 */
    @Nullable
    private Boolean assign = null;

    /** 是否可继承, 默认否 */
    @Nullable
    private Boolean inheritable = null;

    /** 是否启用双因素认证, 默认否 */
    @Nullable
    private Boolean mfa = null;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AssignPermissionArgs that = (AssignPermissionArgs) object;
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(permissionId);
    }
}
