package cn.sh.ideal.iam.security.api.adapter;

import cn.sh.ideal.iam.security.api.TenantAccessibility;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/1
 */
public interface TenantAccessibilityFactory {
    /**
     * 获取租户可访问性
     *
     * @param userId 用户ID
     * @return 租户可访问性
     */
    TenantAccessibility createTenantAccessibility(@Nonnull Long userId);
}
