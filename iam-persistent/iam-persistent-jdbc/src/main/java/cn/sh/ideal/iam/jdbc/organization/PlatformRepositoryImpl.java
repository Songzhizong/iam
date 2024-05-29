package cn.sh.ideal.iam.jdbc.organization;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.organization.domain.model.Platform;
import cn.sh.ideal.iam.organization.domain.model.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PlatformRepositoryImpl implements PlatformRepository {
    private final IamI18nReader i18nReader;
    private final PlatformJpaRepository platformJpaRepository;

    @Nonnull
    @Override
    public Platform insert(@Nonnull Platform platform) {
        PlatformDO entity = (PlatformDO) platform;
        return platformJpaRepository.save(entity);
    }

    @Nonnull
    @Override
    public Platform update(@Nonnull Platform platform) {
        PlatformDO entity = (PlatformDO) platform;
        return platformJpaRepository.save(entity);
    }

    @Nonnull
    @Override
    public Optional<Platform> findByCode(@Nonnull String code) {
        return platformJpaRepository.findByCode(code).map(e -> e);
    }

    @Nonnull
    @Override
    public List<Platform> findAll() {
        return platformJpaRepository.findAllByDeleted(false)
                .stream().map(e -> (Platform) e).toList();
    }

    @Nonnull
    @Override
    public Platform requireByCode(@Nonnull String code) {
        return findByCode(code).orElseThrow(() -> {
            log.info("获取平台信息失败, 平台不存在: {}", code);
            return new ResourceNotFoundException(i18nReader.getMessage("platform.not.found"));
        });
    }
}
