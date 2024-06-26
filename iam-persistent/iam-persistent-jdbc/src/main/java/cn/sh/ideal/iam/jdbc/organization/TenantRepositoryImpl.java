package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {
    private final IamI18nReader i18nReader;
    private final TenantJpaRepository tenantJpaRepository;

    @Nonnull
    @Override
    public Tenant insert(@Nonnull Tenant tenant) {
        TenantDO entity = (TenantDO) tenant;
        return tenantJpaRepository.saveAndFlush(entity);
    }

    @Nonnull
    @Override
    public Tenant save(@Nonnull Tenant tenant) {
        TenantDO entity = (TenantDO) tenant;
        return tenantJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(@Nonnull Tenant tenant) {
        TenantDO entity = (TenantDO) tenant;
        tenantJpaRepository.delete(entity);
    }

    @Nonnull
    @Override
    public Optional<Tenant> findById(@Nonnull Long id) {
        return tenantJpaRepository.findById(id).map(e -> e);
    }

    @Nonnull
    @Override
    public Optional<Tenant> findByPlatformAndAbbreviation(@Nonnull String platform,
                                                          @Nonnull String abbreviation) {
        return tenantJpaRepository.findByPlatformAndAbbreviation(platform, abbreviation).map(e -> e);
    }

    @Nonnull
    @Override
    public List<Tenant> findAllByContainerIdIn(@Nonnull Collection<Long> containerIds) {
        if (containerIds.isEmpty()) {
            return List.of();
        }
        return tenantJpaRepository.findAllByContainerIdIn(containerIds)
                .stream().map(e -> (Tenant) e).toList();
    }

    @Override
    public boolean existsByContainerId(@Nonnull Long containerId) {
        return tenantJpaRepository.existsByContainerId(containerId);
    }

    @Override
    public boolean existsByPlatformAndAbbreviation(@Nonnull String platform,
                                                   @Nonnull String abbreviation) {
        return tenantJpaRepository.existsByPlatformAndAbbreviation(platform, abbreviation);
    }

    @Nonnull
    @Override
    public Tenant requireById(@Nonnull Long id) {
        return findById(id).orElseThrow(() -> {
            String[] args = {String.valueOf(id)};
            return new ResourceNotFoundException(i18nReader.getMessage("tenant.notfound", args));
        });
    }

}
