package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
import cn.sh.ideal.iam.organization.domain.model.Group;
import cn.sh.ideal.iam.organization.domain.model.User;
import cn.sh.ideal.iam.organization.domain.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2024/5/15
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final IamIDGenerator idGenerator;
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final UserGroupRelJpaRepository userGroupRelJpaRepository;

    @Nonnull
    @Override
    public User insert(@Nonnull User user) {
        UserDO entity = (UserDO) user;
        entity.setId(idGenerator.generate());
        return userJpaRepository.saveAndFlush(entity);
    }

    @Nonnull
    @Override
    public User save(@Nonnull User user) {
        UserDO entity = (UserDO) user;
        return userJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(@Nonnull User user) {
        // 删除用户的同时要删除用户和用户组之间的关系
        long userId = user.getId();
        userGroupRelJpaRepository.deleteAllByUserId(userId);
        // 删除用户
        UserDO entity = (UserDO) user;
        userJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<User> findById(long id) {
        return userJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<User> findAllById(@Nonnull Collection<Long> ids) {
        return userJpaRepository.findAllById(ids)
                .stream().map(e -> (User) e).toList();
    }

    @Override
    public boolean existsByTenantIdAndAccount(long tenantId, @Nonnull String account) {
        return userJpaRepository.existsByTenantIdAndAccount(tenantId, account);
    }

    @Nonnull
    @Override
    public List<Group> getGroups(long userId) {
        List<UserGroupRelDO> relList = userGroupRelJpaRepository.findAllByUserId(userId);
        if (relList.isEmpty()) {
            return List.of();
        }
        Set<Long> groupIds = relList.stream().map(UserGroupRelDO::getGroupId).collect(Collectors.toSet());
        return groupJpaRepository.findAllById(groupIds)
                .stream().map(e -> (Group) e).toList();
    }

    @Override
    public void saveGroups(long userId, @Nonnull Collection<Group> groups) {
        userGroupRelJpaRepository.deleteAllByUserId(userId);
        if (groups.isEmpty()) {
            return;
        }
        List<UserGroupRelDO> entities = groups.stream().map(group -> {
            Long groupId = group.getId();
            return UserGroupRelDO.create(userId, groupId);
        }).toList();
        userGroupRelJpaRepository.saveAll(entities);
    }
}
