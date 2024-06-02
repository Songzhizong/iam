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
    Optional<User> findById(@Nonnull Long id);

    @Nonnull
    List<User> findAllById(@Nonnull Collection<Long> ids);

    @Nonnull
    Optional<User> findByPlatformAndEmail(@Nonnull String platform, @Nonnull String email);

    @Nonnull
    Optional<User> findByTenantIdAndAccount(@Nonnull Long tenantId, @Nonnull String account);

    boolean existsByTenantIdAndAccount(@Nonnull Long tenantId, @Nonnull String account);

    /** 获取用户关联的所有用户组 */
    @Nonnull
    List<UserGroup> getGroups(@Nonnull Long userId);

    /** 获取用户关联的所有用户组id */
    @Nonnull
    List<Long> getGroupIds(@Nonnull Long userId);

    /** 保存用户关联的用户组 */
    void saveGroups(@Nonnull Long userId, @Nonnull Collection<UserGroup> groups);

    @Nonnull
    User requireById(@Nonnull Long id);
}
