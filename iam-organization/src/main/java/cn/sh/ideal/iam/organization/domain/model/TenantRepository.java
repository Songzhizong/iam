package cn.sh.ideal.iam.organization.domain.model;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;

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
    default Tenant requireById(long id, @Nonnull IamI18nReader i18nReader) {
        return findById(id).orElseThrow(() -> {
            String[] args = {String.valueOf(id)};
            return new ResourceNotFoundException(i18nReader.getMessage("tenant.notfound", args));
        });
    }
}
