package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
import cn.sh.ideal.iam.organization.domain.model.Group;
import cn.sh.ideal.iam.organization.domain.model.GroupRepository;
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
public class GroupRepositoryImpl implements GroupRepository {
    private final IamIDGenerator idGenerator;
    private final GroupJpaRepository groupJpaRepository;
    private final UserGroupRelJpaRepository userGroupRelJpaRepository;


    @Nonnull
    @Override
    public Group insert(@Nonnull Group group) {
        GroupDO entity = (GroupDO) group;
        entity.setId(idGenerator.generate());
        return groupJpaRepository.saveAndFlush(entity);
    }

    @Nonnull
    @Override
    public Group save(@Nonnull Group group) {
        GroupDO entity = (GroupDO) group;
        return groupJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(@Nonnull Group group) {
        // 删除用户组的同时要删除用户组和用户之间的关系
        long groupId = group.getId();
        userGroupRelJpaRepository.deleteAllByGroupId(groupId);
        // 删除用户组
        GroupDO entity = (GroupDO) group;
        groupJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<Group> findById(long id) {
        return groupJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<Group> findAllById(@Nonnull Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return groupJpaRepository.findAllById(ids)
                .stream().map(e -> (Group) e).toList();
    }
}
