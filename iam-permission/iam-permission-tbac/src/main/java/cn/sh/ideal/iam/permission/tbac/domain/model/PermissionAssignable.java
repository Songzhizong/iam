package cn.sh.ideal.iam.permission.tbac.domain.model;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class PermissionAssignable {

    public static final PermissionAssignable EMPTY = new PermissionAssignable() {
        @Override
        public void setGroupIds(@Nonnull Set<Long> groupIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setItemIds(@Nonnull Set<Long> itemIds) {
            throw new UnsupportedOperationException();
        }
    };

    @Nonnull
    private Set<Long> groupIds = Set.of();

    @Nonnull
    private Set<Long> itemIds = Set.of();
}
