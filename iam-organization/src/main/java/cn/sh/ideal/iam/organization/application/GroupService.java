package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.audit.Audits;
import cn.idealio.framework.audit.Fields;
import cn.sh.ideal.iam.infrastructure.constant.AuditConstants;
import cn.sh.ideal.iam.infrastructure.permission.tbac.SecurityContainerValidator;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.*;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {
    private final EntityFactory entityFactory;
    private final OrganizationI18nReader i18nReader;
    private final TenantRepository tenantRepository;
    private final UserGroupRepository userGroupRepository;
    @Nullable
    @Autowired(required = false)
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    private final SecurityContainerValidator securityContainerValidator;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public UserGroup create(long tenantId, @Nonnull CreateGroupArgs args) {
        Tenant tenant = tenantRepository.requireById(tenantId, i18nReader);
        Long tenantContainerId = tenant.getContainerId();
        Long containerId = args.getContainerId();
        if (containerId == null) {
            // 如果未指定安全容器ID, 则直接使用所属租户的安全容器ID
            containerId = tenantContainerId;
        }
        if (containerId != null) {
            if (securityContainerValidator != null) {
                securityContainerValidator.requireExits(containerId);
            }
        }
        args.setContainerId(containerId);
        UserGroup group = entityFactory.group(tenant, args, i18nReader);
        UserGroup insert = userGroupRepository.insert(group);
        entityAudit(insert);
        return insert;
    }

    @Nullable
    @Transactional(rollbackFor = Throwable.class)
    public UserGroup delete(long id) {
        UserGroup group = userGroupRepository.findById(id).orElse(null);
        if (group == null) {
            log.info("删除的用户组[{}]不存在", id);
            return null;
        }
        userGroupRepository.delete(group);
        entityAudit(group);
        return group;
    }

    private static void entityAudit(@Nonnull UserGroup group) {
        Audits.modify(audit -> {
            audit.containerId(group.getContainerId());
            audit.resourceType(AuditConstants.USER_GROUP);
            audit.resourceName(group.getName());
            audit.resourceTenantId(group.getTenantId());
            audit.auditInfo(Fields.of().add("名称", "name", group.getName()));
        });
    }
}
