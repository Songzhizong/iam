package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.audit.Audits;
import cn.idealio.framework.audit.Fields;
import cn.idealio.framework.concurrent.Asyncs;
import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.NumberSystemConverter;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.infrastructure.permission.tbac.SecurityContainerValidator;
import cn.sh.ideal.iam.organization.domain.model.*;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {
    private static final long MAX_TIME = 4765107660000L;
    private static final Duration INVALIDATE_CACHE_DELAY = Duration.ofSeconds(2);
    private final TenantCache tenantCache;
    private final IamI18nReader i18nReader;
    private final IamIDGenerator idGenerator;
    private final TenantRepository tenantRepository;
    private final PlatformRepository platformRepository;
    private final OrganizationEntityFactory entityFactory;
    @Nullable
    @Autowired(required = false)
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    private final SecurityContainerValidator securityContainerValidator;

    @Transactional(rollbackFor = Throwable.class)
    public Tenant create(@Nonnull String platform,
                         @Nonnull CreateTenantArgs args) {
        String abbreviation = args.getAbbreviation();
        if (StringUtils.isNotBlank(abbreviation)) {
            if (tenantRepository.existsByPlatformAndAbbreviation(platform, abbreviation)) {
                log.info("创建租户失败, 租户缩写已存在: {}", abbreviation);
                throw new BadRequestException(i18nReader.getMessage("tenant.abbreviation.exists"));
            }
        } else {
            abbreviation = generateAbbreviation(platform);
        }
        Long containerId = args.getContainerId();
        if (containerId != null) {
            if (securityContainerValidator != null) {
                securityContainerValidator.requireExits(containerId);
            }
        }
        args.setAbbreviation(abbreviation);
        long id = idGenerator.generate();
        Platform platformEntity = platformRepository.requireByCode(platform);
        Tenant tenant = entityFactory.tenant(id, platformEntity, args, i18nReader);
        Tenant insert = tenantRepository.insert(tenant);
        Audits.modify(audit -> {
            audit.containerId(insert.getContainerId());
            audit.resourceType(AuditConstants.TENANT);
            audit.resourceName(insert.getName());
            audit.resourceTenantId(insert.getId());
            audit.auditInfo(Fields.of().add("名称", "name", insert.getName()));
        });
        Asyncs.execAndDelayVirtual(INVALIDATE_CACHE_DELAY, () -> tenantCache.invalidate(insert.getId()));
        return insert;
    }

    @Nonnull
    private String generateAbbreviation(@Nonnull String platform) {
        String abbreviation;
        do {
            abbreviation = NumberSystemConverter.to26(MAX_TIME - System.currentTimeMillis());
            if (!tenantRepository.existsByPlatformAndAbbreviation(platform, abbreviation)) {
                return abbreviation;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
        } while (true);
    }
}
