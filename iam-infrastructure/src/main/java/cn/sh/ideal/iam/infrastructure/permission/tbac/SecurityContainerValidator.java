package cn.sh.ideal.iam.infrastructure.permission.tbac;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface SecurityContainerValidator {

    void requireExits(long containerId);
}
