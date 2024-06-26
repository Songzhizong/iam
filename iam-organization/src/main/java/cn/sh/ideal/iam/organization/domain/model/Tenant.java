package cn.sh.ideal.iam.organization.domain.model;

import cn.sh.ideal.iam.organization.dto.resp.TenantInfo;
import cn.sh.ideal.iam.security.api.AccessibleTenant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface Tenant {

    /** 主键 */
    @Nonnull
    Long getId();

    @Nonnull
    String getPlatform();

    /** 安全容器ID */
    @Nullable
    Long getContainerId();

    /** 名称 */
    @Nonnull
    String getName();

    /** 缩写 */
    @Nonnull
    String getAbbreviation();

    /** 描述信息 */
    @Nullable
    String getNote();

    /** 系统版本编码 */
    @Nullable
    String getSystemEdition();

    /** 是否已被冻结 */
    boolean isBlocked();

    @Nonnull
    default TenantInfo toInfo() {
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setId(getId());
        tenantInfo.setContainerId(getContainerId());
        tenantInfo.setName(getName());
        tenantInfo.setAbbreviation(getAbbreviation());
        tenantInfo.setNote(getNote());
        tenantInfo.setSystemEdition(getSystemEdition());
        return tenantInfo;
    }

    @Nonnull
    default AccessibleTenant toAccessibleTenant() {
        AccessibleTenant accessibleTenant = new AccessibleTenant();
        accessibleTenant.setId(getId());
        accessibleTenant.setName(getName());
        accessibleTenant.setAbbreviation(getAbbreviation());
        return accessibleTenant;
    }
}
