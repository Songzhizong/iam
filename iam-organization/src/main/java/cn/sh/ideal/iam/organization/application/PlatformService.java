package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
import cn.sh.ideal.iam.organization.domain.model.OrganizationEntityFactory;
import cn.sh.ideal.iam.organization.domain.model.Platform;
import cn.sh.ideal.iam.organization.domain.model.PlatformRepository;
import cn.sh.ideal.iam.organization.dto.args.CreatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.args.UpdatePlatformArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformService {
    private final IamI18nReader i18nReader;
    private final IamIDGenerator idGenerator;
    private final PlatformRepository platformRepository;
    private final OrganizationEntityFactory entityFactory;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public Platform create(@Nonnull CreatePlatformArgs args) {
        String code = args.getCode();
        Asserts.notBlank(code, () -> i18nReader.getMessage("platform.code.required"));
        platformRepository.findByCode(code).ifPresent(e -> {
            log.info("平台编码已被使用: {}", code);
            throw new BadRequestException(i18nReader.getMessage("platform.code.exists"));
        });
        long id = idGenerator.generate();
        Platform platform = entityFactory.platform(id, args, i18nReader);
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
        return platformRepository.update(platform);
    }

    @Nonnull
    public List<Platform> findAll() {
        return platformRepository.findAll();
    }
}
