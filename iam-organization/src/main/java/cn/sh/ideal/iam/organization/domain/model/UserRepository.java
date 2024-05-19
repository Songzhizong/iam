package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface UserRepository {

    @Nonnull
    User insert(@Nonnull User user);

    @Nonnull
    User save(@Nonnull User user);

    void delete(@Nonnull User user);

    @Nonnull
    Optional<User> findById(long id);

    @Nonnull
    List<User> findAllById(@Nonnull Collection<Long> ids);

    boolean existsByTenantIdAndAccount(long tenantId, @Nonnull String account);

    /** 获取用户关联的所有用户组 */
    @Nonnull
    List<Group> getGroups(long userId);

    /** 获取用户关联的所有用户组id */
    @Nonnull
    List<Long> getGroupIds(long userId);

    /** 保存用户关联的用户组 */
    void saveGroups(long userId, @Nonnull Collection<Group> groups);
}
