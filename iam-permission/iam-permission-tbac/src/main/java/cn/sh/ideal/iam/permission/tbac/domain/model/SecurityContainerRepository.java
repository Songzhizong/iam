package cn.sh.ideal.iam.permission.tbac.domain.model;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface SecurityContainerRepository {
    List<SecurityContainerRepositoryListener> listeners = new ArrayList<>();

    default void addListener(@Nonnull SecurityContainerRepositoryListener listener) {
        listeners.add(listener);
    }

    @Nonnull
    SecurityContainer insert(@Nonnull SecurityContainer securityContainer);

    @Nonnull
    SecurityContainer save(@Nonnull SecurityContainer securityContainer);

    void save(@Nonnull Collection<SecurityContainer> securityContainers);

    void delete(@Nonnull SecurityContainer securityContainer);

    @Nonnull
    Optional<SecurityContainer> findById(long id);

    @Nonnull
    List<SecurityContainer> findAll();

    @Nonnull
    List<SecurityContainer> findAllById(@Nonnull Collection<Long> ids);

    @Nonnull
    List<SecurityContainer> findAllByParentIdIn(@Nonnull Collection<Long> parentIds);

    boolean exists();

    boolean existsByParentId(long parentId);

    boolean existsByParentIdAndName(@Nullable Long parentId, @Nonnull String name);

    @Nonnull
    default SecurityContainer requireById(long id, @Nonnull IamI18nReader i18nReader) {
        return findById(id).orElseThrow(() -> {
            String[] args = {String.valueOf(id)};
            return new ResourceNotFoundException(i18nReader.getMessage("sc.notfound", args));
        });
    }
}
