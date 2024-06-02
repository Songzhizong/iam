package cn.sh.ideal.iam.security.api;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Getter
@Setter
public class AccessibleTenant implements Comparable<AccessibleTenant> {

    /** 租户ID */
    @Nonnull
    private Long id;

    /** 租户名称 */
    @Nonnull
    private String name;

    /** 租户缩写 */
    @Nonnull
    private String abbreviation;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AccessibleTenant that = (AccessibleTenant) object;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @Override
    public int compareTo(@Nonnull AccessibleTenant o) {
        // 按abbreviation升序排序
        return abbreviation.compareTo(o.abbreviation);
    }
}
