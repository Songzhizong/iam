package cn.sh.ideal.iam.organization.domain.model;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface ContainerTenantCount {

    /** 安全容器ID */
    long getContainerId();

    int getCount();
}
