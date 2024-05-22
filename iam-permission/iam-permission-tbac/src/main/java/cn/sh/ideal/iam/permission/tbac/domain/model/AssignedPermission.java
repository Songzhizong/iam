package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author 宋志宗 on 2024/5/18
 */
@Getter
public class AssignedPermission {

    /** 是否启用多因素认证 */
    private boolean mfa;

    /** 权限信息 */
    @Nonnull
    private final Permission permission;

    public AssignedPermission(@Nonnull PermissionAssignDetail assignDetail) {
        this.mfa = assignDetail.isMfa();
        this.permission = assignDetail.getPermission();
    }

    public void updateMfa(boolean mfa) {
        // 只要任意一个分配关系启用双因素认证，则启用双因素认证
        this.mfa = this.mfa || mfa;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AssignedPermission detail = (AssignedPermission) object;
        return Objects.equals(permission.getId(), detail.getPermission().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(permission.getId());
    }
}
