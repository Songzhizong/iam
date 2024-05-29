package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.concurrent.Asyncs;
import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.EntityFactory;
import cn.sh.ideal.iam.organization.domain.model.Platform;
import cn.sh.ideal.iam.organization.domain.model.PlatformCache;
import cn.sh.ideal.iam.organization.domain.model.PlatformRepository;
import cn.sh.ideal.iam.organization.dto.args.CreatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.args.UpdatePlatformArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformService {
    private static final Duration INVALIDATE_CACHE_DELAY = Duration.ofSeconds(2);
    private final PlatformCache cache;
    private final EntityFactory entityFactory;
    private final OrganizationI18nReader i18nReader;
    private final PlatformRepository platformRepository;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public Platform create(@Nonnull CreatePlatformArgs args) {
        String code = args.getCode();
        Asserts.notBlank(code, () -> i18nReader.getMessage("platform.code.required"));
        platformRepository.findByCode(code).ifPresent(e -> {
            log.info("平台编码已被使用: {}", code);
            throw new BadRequestException(i18nReader.getMessage("platform.code.exists"));
        });
        Platform platform = entityFactory.platform(args, i18nReader);
        return platformRepository.insert(platform);
    }

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public Platform update(@Nonnull String code,
                           @Nonnull UpdatePlatformArgs args) {
        Platform platform = platformRepository.findByCode(code).orElseThrow(() -> {
            log.info("更新平台信息失败, 平台不存在: {}", code);
            return new ResourceNotFoundException(i18nReader.getMessage("platform.not.found"));
        });
        platform.update(args, i18nReader);
        Asyncs.execAndDelayVirtual(INVALIDATE_CACHE_DELAY, () -> cache.invalidate(code));
        return platformRepository.update(platform);
    }

    @Nullable
    @Transactional(rollbackFor = Throwable.class)
    public Platform delete(@Nonnull String code) {
        Platform platform = platformRepository.findByCode(code).orElse(null);
        if (platform == null) {
            log.info("删除平台信息失败, 平台不存在: {}", code);
            return null;
        }
        platform.delete();
        Asyncs.execAndDelayVirtual(INVALIDATE_CACHE_DELAY, () -> cache.invalidate(code));
        return platformRepository.update(platform);
    }

    @Nonnull
    public List<Platform> findAll() {
        return platformRepository.findAll();
    }
}
