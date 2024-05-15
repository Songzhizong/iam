package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface GroupRepository {

    @Nonnull
    Group insert(@Nonnull Group group);

    @Nonnull
    Group save(@Nonnull Group group);

    void delete(@Nonnull Group group);

    @Nonnull
    Optional<Group> findById(long id);

    @Nonnull
    List<Group> findAllById(@Nonnull Collection<Long> ids);
}
