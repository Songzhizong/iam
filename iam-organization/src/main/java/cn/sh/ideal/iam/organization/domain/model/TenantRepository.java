package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/5/14
 */
public interface TenantRepository {

    @Nonnull
    Tenant insert(@Nonnull Tenant tenant);

    @Nonnull
    Tenant save(@Nonnull Tenant tenant);

    void delete(@Nonnull Tenant tenant);

    @Nonnull
    Optional<Tenant> findById(@Nonnull Long id);

    @Nonnull
    Optional<Tenant> findByPlatformAndAbbreviation(@Nonnull String platform,
                                                   @Nonnull String abbreviation);

    @Nonnull
    List<Tenant> findAllByContainerIdIn(@Nonnull Collection<Long> containerIds);

    boolean existsByContainerId(@Nonnull Long containerId);

    boolean existsByPlatformAndAbbreviation(@Nonnull String platform,
                                            @Nonnull String abbreviation);

    @Nonnull
    Tenant requireById(@Nonnull Long id);
}
