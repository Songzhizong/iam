package cn.sh.ideal.iam.organization.domain.model;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SequencedSet;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface SecurityContainerRepository {

    @Nonnull
    SecurityContainer insert(@Nonnull SecurityContainer securityContainer);

    @Nonnull
    SecurityContainer save(@Nonnull SecurityContainer securityContainer);

    void save(@Nonnull Collection<SecurityContainer> securityContainers);

    void delete(@Nonnull SecurityContainer securityContainer);

    @Nonnull
    Optional<SecurityContainer> findById(long id);

    @Nonnull
    List<SecurityContainer> findAllById(@Nonnull Collection<Long> ids);

    @Nonnull
    List<SecurityContainer> findAllByParentIdIn(@Nonnull Collection<Long> parentIds);

    boolean exists();

    boolean existsByParentId(long parentId);

    boolean existsByParentIdAndName(@Nullable Long parentId, @Nonnull String name);

    @Nonnull
    default SecurityContainer requireById(long id, @Nonnull OrganizationI18nReader i18nReader) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException(i18nReader.getMessage("sc.notfound", new Object[]{id})));
    }

    default boolean isChild(long childId, long containerId, @Nonnull OrganizationI18nReader i18nReader) {
        // 如果指定了安全容器ID, 且安全容器ID和租户所属安全容器ID不同, 则判断是否为租户所属安全容器的子容器
        SecurityContainer container = requireById(childId, i18nReader);
        SequencedSet<Long> parentIds = container.parentIds();
        return parentIds.contains(containerId);
    }

}
