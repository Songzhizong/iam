package cn.sh.ideal.iam.jdbc.organization;

import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {
    private final IamIDGenerator idGenerator;
    private final TenantJpaRepository tenantJpaRepository;

    @Nonnull
    @Override
    public Tenant insert(@Nonnull Tenant tenant) {
        TenantDO entity = (TenantDO) tenant;
        entity.setId(idGenerator.generate());
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
    public Optional<Tenant> findById(long id) {
        return tenantJpaRepository.findById(id).map(e -> e);
    }

    @Override
    public boolean existsByContainerId(long containerId) {
        return tenantJpaRepository.existsByContainerId(containerId);
    }

    @Override
    public boolean existsByAbbreviation(@Nonnull String abbreviation) {
        return tenantJpaRepository.existsByAbbreviation(abbreviation);
    }

}
