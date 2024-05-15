package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.util.NumberSystemConverter;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.EntityFactory;
import cn.sh.ideal.iam.organization.domain.model.Tenant;
import cn.sh.ideal.iam.organization.domain.model.TenantRepository;
import cn.sh.ideal.iam.organization.dto.args.CreateTenantArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {
    private static final long MAX_TIME = 4765107660000L;
    private final EntityFactory entityFactory;
    private final TenantRepository tenantRepository;
    private final OrganizationI18nReader i18nReader;

    @Transactional(rollbackFor = Throwable.class)
    public Tenant create(@Nonnull CreateTenantArgs args) {
        String abbreviation = args.getAbbreviation();
        if (StringUtils.isNotBlank(abbreviation)) {
            if (tenantRepository.existsByAbbreviation(abbreviation)) {
                log.info("创建租户失败, 租户缩写已存在: {}", abbreviation);
                throw new BadRequestException(i18nReader.getMessage("tenant.abbreviation.exists"));
            }
        } else {
            abbreviation = generateAbbreviation();
        }
        args.setAbbreviation(abbreviation);
        Tenant tenant = entityFactory.tenant(args, i18nReader);
        return tenantRepository.insert(tenant);
    }

    @Nonnull
    private String generateAbbreviation() {
        String abbreviation;
        do {
            abbreviation = NumberSystemConverter.to26(MAX_TIME - System.currentTimeMillis());
            if (!tenantRepository.existsByAbbreviation(abbreviation)) {
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
