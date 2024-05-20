package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface UserGroupRepository {

    @Nonnull
    UserGroup insert(@Nonnull UserGroup group);

    @Nonnull
    UserGroup save(@Nonnull UserGroup group);

    void delete(@Nonnull UserGroup group);

    @Nonnull
    Optional<UserGroup> findById(long id);

    @Nonnull
    List<UserGroup> findAllById(@Nonnull Collection<Long> ids);
}
