package cn.sh.ideal.iam.security.api;

/**
 * 租户可访问性
 *
 * @author 宋志宗 on 2024/6/1
 */
public interface TenantAccessibility {

    /**
     * 判断租户是否可访问
     *
     * @param tenantId 租户ID
     * @return 是否可访问
     */
    boolean isAccessible(long tenantId);
}
