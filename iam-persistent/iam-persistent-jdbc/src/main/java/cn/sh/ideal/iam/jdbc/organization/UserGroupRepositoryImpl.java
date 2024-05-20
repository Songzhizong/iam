package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.organization.domain.model.UserGroup;
import cn.sh.ideal.iam.organization.domain.model.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/15
 */
@Repository
@RequiredArgsConstructor
public class UserGroupRepositoryImpl implements UserGroupRepository {
    private final UserGroupJpaRepository userGroupJpaRepository;
    private final UserGroupRelJpaRepository userGroupRelJpaRepository;


    @Nonnull
    @Override
    public UserGroup insert(@Nonnull UserGroup group) {
        UserGroupDO entity = (UserGroupDO) group;
        return userGroupJpaRepository.saveAndFlush(entity);
    }

    @Nonnull
    @Override
    public UserGroup save(@Nonnull UserGroup group) {
        UserGroupDO entity = (UserGroupDO) group;
        return userGroupJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(@Nonnull UserGroup group) {
        // 删除用户组的同时要删除用户组和用户之间的关系
        long groupId = group.getId();
        userGroupRelJpaRepository.deleteAllByGroupId(groupId);
        // 删除用户组
        UserGroupDO entity = (UserGroupDO) group;
        userGroupJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<UserGroup> findById(long id) {
        return userGroupJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<UserGroup> findAllById(@Nonnull Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return userGroupJpaRepository.findAllById(ids)
                .stream().map(e -> (UserGroup) e).toList();
    }
}
