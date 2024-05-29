package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.audit.Audits;
import cn.idealio.framework.audit.Fields;
import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.infrastructure.permission.tbac.SecurityContainerValidator;
import cn.sh.ideal.iam.organization.domain.model.*;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final IamI18nReader i18nReader;
    private final EntityFactory entityFactory;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserGroupRepository userGroupRepository;
    @Nullable
    @Autowired(required = false)
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    private final SecurityContainerValidator securityContainerValidator;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public User create(long tenantId, @Nonnull CreateUserArgs args) {
        // 安全容器处理
        Tenant tenant = tenantRepository.requireById(tenantId, i18nReader);
        Long tenantContainerId = tenant.getContainerId();
        Long containerId = args.getContainerId();
        if (containerId == null) {
            // 如果未指定安全容器ID, 则直接使用所属租户的安全容器ID
            containerId = tenantContainerId;
        }
        args.setContainerId(containerId);
        if (containerId != null) {
            if (securityContainerValidator != null) {
                securityContainerValidator.requireExits(containerId);
            }
        }

        // 验证账号是否已被使用
        String account = args.getAccount();
        if (StringUtils.isNotBlank(account)
                && userRepository.existsByTenantIdAndAccount(tenantId, account)) {
            log.info("新增用户失败, 账号已被使用: {}", account);
            throw new BadRequestException(i18nReader.getMessage("user.account_used"));
        }

        User user = entityFactory.user(tenant, args, i18nReader);
        user = userRepository.insert(user);

        Set<Long> groupIds = args.getUserGroupIds();
        if (groupIds != null) {
            List<UserGroup> groups = userGroupRepository.findAllById(groupIds);
            userRepository.saveGroups(user.getId(), groups);
        }
        entityAudit(user);
        return user;
    }

    private static void entityAudit(@Nonnull User user) {
        Audits.modify(audit -> {
            String name = user.getName();
            String account = user.getAccount();
            Fields fields = Fields.of().add("姓名", "name", name);
            if (StringUtils.isNotBlank(account)) {
                fields.add("账号", "account", account);
            }
            audit.containerId(user.getContainerId());
            audit.resourceType(AuditConstants.USER);
            audit.resourceName(name);
            audit.resourceTenantId(user.getTenantId());
            audit.auditInfo(fields);
        });
    }
}
