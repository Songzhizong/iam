package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.sh.ideal.iam.permission.front.domain.model.Permission;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author 宋志宗 on 2024/5/18
 */
@Getter
public class PermissionAssignDetail {

    /** 是否启用多因素认证 */
    private boolean mfa;

    /** 是否分配权限 */
    private boolean assigned;

    /** 下级节点是否可继承此权限 */
    private boolean inheritable;

    /** 权限信息 */
    @Nonnull
    private final Permission permission;

    public PermissionAssignDetail(boolean mfa,
                                  boolean assigned,
                                  boolean inheritable,
                                  @Nonnull Permission permission) {
        this.mfa = mfa;
        this.assigned = assigned;
        this.inheritable = inheritable;
        this.permission = permission;
    }

    public void setMfa(boolean mfa) {
        // 只要任意一个分配关系启用双因素认证，则启用双因素认证
        this.mfa = this.mfa || mfa;
    }

    public void setAssigned(boolean assigned) {
        // 只要任意一个分配关系启用授权，则启用授权
        this.assigned = this.assigned || assigned;
    }

    public void setInheritable(boolean inheritable) {
        // 只要任意一个分配关系启用继承，则启用继承
        this.inheritable = this.inheritable || inheritable;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PermissionAssignDetail detail = (PermissionAssignDetail) object;
        return permission.getId().equals(detail.getPermission().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(permission.getId());
    }
}
