package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.*;
import cn.sh.ideal.iam.organization.dto.args.CreateUserArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final EntityFactory entityFactory;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TenantRepository tenantRepository;
    private final OrganizationI18nReader i18nReader;
    private final SecurityContainerRepository securityContainerRepository;

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
            securityContainerRepository.requireById(containerId, i18nReader);
        }

        // 验证账号是否已被使用
        String account = args.getAccount();
        if (StringUtils.isNotBlank(account) && userRepository.existsByTenantIdAndAccount(tenantId, account)) {
            log.info("新增用户失败, 账号已被使用: {}", account);
            throw new BadRequestException(i18nReader.getMessage("user.account_used"));
        }

        User user = entityFactory.user(tenantId, args, i18nReader);
        user = userRepository.insert(user);

        Set<Long> groupIds = args.getUserGroupIds();
        if (groupIds != null) {
            List<Group> groups = groupRepository.findAllById(groupIds);
            userRepository.saveGroups(user.getId(), groups);
        }
        return user;
    }
}
