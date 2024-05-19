package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.organization.domain.model.SecurityContainer;
import cn.sh.ideal.iam.organization.domain.model.SecurityContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Repository
@RequiredArgsConstructor
public class SecurityContainerRepositoryImpl implements SecurityContainerRepository {
    private final SecurityContainerJpaRepository securityContainerJpaRepository;

    @Nonnull
    @Override
    public SecurityContainer insert(@Nonnull SecurityContainer securityContainer) {
        SecurityContainerDO entity = (SecurityContainerDO) securityContainer;
        return securityContainerJpaRepository.saveAndFlush(entity);
    }

    @Nonnull
    @Override
    public SecurityContainer save(@Nonnull SecurityContainer securityContainer) {
        SecurityContainerDO entity = (SecurityContainerDO) securityContainer;
        return securityContainerJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void save(@Nonnull Collection<SecurityContainer> securityContainers) {
        securityContainers.forEach(this::save);
    }

    @Override
    public void delete(@Nonnull SecurityContainer securityContainer) {
        SecurityContainerDO entity = (SecurityContainerDO) securityContainer;
        securityContainerJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<SecurityContainer> findById(long id) {
        return securityContainerJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public List<SecurityContainer> findAll() {
        return securityContainerJpaRepository.findAll()
                .stream().map(e -> (SecurityContainer) e).toList();
    }

    @Nonnull
    @Override
    public List<SecurityContainer> findAllById(@Nonnull Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return securityContainerJpaRepository.findAllById(ids)
                .stream().map(e -> (SecurityContainer) e).toList();
    }

    @Nonnull
    @Override
    public List<SecurityContainer> findAllByParentIdIn(@Nonnull Collection<Long> parentIds) {
        return securityContainerJpaRepository.findAllByParentIdIn(parentIds)
                .stream().map(e -> (SecurityContainer) e).toList();
    }

    @Override
    public boolean exists() {
        return securityContainerJpaRepository.existsByIdGreaterThanEqual(0);
    }

    @Override
    public boolean existsByParentId(long parentId) {
        return securityContainerJpaRepository.existsByParentId(parentId);
    }

    @Override
    public boolean existsByParentIdAndName(@Nullable Long parentId, @Nonnull String name) {
        if (parentId == null) {
            parentId = -1L;
        }
        return securityContainerJpaRepository.existsByParentIdAndName(parentId, name);
    }

    @Override
    public boolean existsByUpdatedTimeGte(long updatedTimeGte) {
        return securityContainerJpaRepository.existsByUpdatedTimeGreaterThanEqual(updatedTimeGte);
    }
}
