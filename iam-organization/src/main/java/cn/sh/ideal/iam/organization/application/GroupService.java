package cn.sh.ideal.iam.organization.application;

import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.*;
import cn.sh.ideal.iam.organization.dto.args.CreateGroupArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final GroupRepository groupRepository;
    private final TenantRepository tenantRepository;
    private final OrganizationI18nReader i18nReader;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public Group create(long tenantId, @Nonnull CreateGroupArgs args) {
        Tenant tenant = tenantRepository.requireById(tenantId, i18nReader);
        Long tenantContainerId = tenant.getContainerId();
        Long containerId = args.getContainerId();
        if (containerId == null) {
            // 如果未指定安全容器ID, 则直接使用所属租户的安全容器ID
            containerId = tenantContainerId;
        }
//        if (containerId != null) {
//            securityContainerRepository.requireById(containerId, i18nReader);
//        }
        args.setContainerId(containerId);
        Group group = entityFactory.group(tenantId, args, i18nReader);
        return groupRepository.insert(group);
    }

    @Nullable
    @Transactional(rollbackFor = Throwable.class)
    public Group delete(long id) {
        Group group = groupRepository.findById(id).orElse(null);
        if (group == null) {
            log.info("删除的用户组[{}]不存在", id);
            return null;
        }
        groupRepository.delete(group);
        return group;
    }
}
