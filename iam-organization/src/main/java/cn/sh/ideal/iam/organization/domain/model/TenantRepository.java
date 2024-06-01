package cn.sh.ideal.iam.organization.domain.model;

import javax.annotation.Nonnull;
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
    Optional<Tenant> findById(long id);

    @Nonnull
    Optional<Tenant> findByPlatformAndAbbreviation(@Nonnull String platform,
                                                   @Nonnull String abbreviation);

    boolean existsByContainerId(long containerId);

    boolean existsByPlatformAndAbbreviation(@Nonnull String platform,
                                            @Nonnull String abbreviation);

    @Nonnull
    Tenant requireById(long id);
}
