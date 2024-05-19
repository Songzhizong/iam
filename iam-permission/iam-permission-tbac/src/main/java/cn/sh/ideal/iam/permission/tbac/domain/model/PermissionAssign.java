package cn.sh.ideal.iam.permission.tbac.domain.model;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface PermissionAssign {

    long getContainerId();

    long getPermissionId();

    boolean isAssigned();

    boolean isInheritable();

    boolean isMfa();
}
