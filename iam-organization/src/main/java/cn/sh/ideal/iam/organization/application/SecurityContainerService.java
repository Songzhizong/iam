package cn.sh.ideal.iam.organization.application;

import cn.idealio.framework.audit.Audits;
import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.lock.GlobalLock;
import cn.idealio.framework.lock.GlobalLockFactory;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.organization.configure.OrganizationI18nReader;
import cn.sh.ideal.iam.organization.domain.model.EntityFactory;
import cn.sh.ideal.iam.organization.domain.model.SecurityContainer;
import cn.sh.ideal.iam.organization.domain.model.SecurityContainerRepository;
import cn.sh.ideal.iam.organization.dto.args.CreateSecurityContainerArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityContainerService implements ApplicationRunner {
    private static final Duration CHANGE_PARENT_LOCK_TIMEOUT = Duration.ofSeconds(30);
    private final String lockValue = UUID.randomUUID().toString().replace("-", "");
    private final EntityFactory entityFactory;
    private final OrganizationI18nReader i18nReader;
    private final GlobalLockFactory globalLockFactory;
    private final SecurityContainerRepository securityContainerRepository;


    @Transactional(rollbackFor = Throwable.class)
    public SecurityContainer create(@Nonnull CreateSecurityContainerArgs args) {
        Long parentId = args.getParentId();
        SecurityContainer parent = null;
        if (parentId != null) {
            parent = securityContainerRepository.findById(parentId).orElseThrow(() -> {
                log.info("创建安全容器失败, 指定的父容器不存在: {}", parentId);
                return new ResourceNotFoundException(i18nReader.getMessage("sc.parent.not_found"));
            });
        }
        String name = args.getName();
        Asserts.notBlank(name, () -> i18nReader.getMessage("sc.name.blank"));
        if (securityContainerRepository.existsByParentIdAndName(parentId, name)) {
            log.info("创建安全容器失败, 安全容器名称已被使用: [{} {}]", parentId, name);
            throw new BadRequestException(i18nReader.getMessage("sc.name.exists"));
        }
        SecurityContainer securityContainer = entityFactory.securityContainer(parent, args, i18nReader);
        return securityContainerRepository.insert(securityContainer);
    }

    @Transactional(rollbackFor = Throwable.class)
    public SecurityContainer rename(long id, @Nullable String name) {
        Asserts.notBlank(name, () -> i18nReader.getMessage("sc.name.blank"));
        SecurityContainer securityContainer = securityContainerRepository.requireById(id, i18nReader);
        if (name.equalsIgnoreCase(securityContainer.getName())) {
            log.info("安全容器名称未发生变更");
            Audits.remove();
            return securityContainer;
        }
        securityContainer.setName(name);
        return securityContainerRepository.save(securityContainer);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void changeParent(long id, @Nullable Long parentId) {
        SecurityContainer entity = securityContainerRepository.requireById(id, i18nReader);
        long rootId = entity.parentIds().stream().findFirst().orElse(id);
        String lockKey = "iam:sc:change_parent:" + rootId;
        GlobalLock lock = globalLockFactory.getLock(lockKey, CHANGE_PARENT_LOCK_TIMEOUT);
        boolean tryLock = lock.tryLock(lockValue);
        if (!tryLock) {
            Audits.remove();
            log.info("获取分布式锁失败, 无法修改父节点. id: {}, rootId: {}", id, rootId);
            throw new BadRequestException(i18nReader.getMessage("sc.change_parent.conflict"));
        }
        try {
            SecurityContainer parent = null;
            if (parentId != null) {
                parent = securityContainerRepository.requireById(parentId, i18nReader);
            }
            entity.changeParent(parent, i18nReader);
            securityContainerRepository.save(entity);
            changeChildrenParent(lock, List.of(entity));
        } finally {
            lock.unlock(lockValue);
        }
    }

    private void changeChildrenParent(@Nonnull GlobalLock lock,
                                      @Nonnull List<SecurityContainer> parents) {
        lock.renewal();
        Map<Long, SecurityContainer> parentMap = parents.stream()
                .collect(Collectors.toMap(SecurityContainer::getId, v -> v));
        List<SecurityContainer> children = securityContainerRepository.findAllByParentIdIn(parentMap.keySet());
        if (children.isEmpty()) {
            return;
        }
        for (SecurityContainer child : children) {
            Long parentId = child.getParentId();
            SecurityContainer parent = parentMap.get(parentId);
            child.changeParent(parent, i18nReader);
        }
        securityContainerRepository.save(children);
        changeChildrenParent(lock, children);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(long id) {
        if (!deletable(id)) {
            log.info("删除安全容器失败, 安全容器[{}]无法被删除", id);
            throw new BadRequestException(i18nReader.getMessage("sc.delete.not_allowed"));
        }
        SecurityContainer securityContainer = securityContainerRepository.requireById(id, i18nReader);
        securityContainerRepository.delete(securityContainer);
        // TODO 发布事件, 删除关联数据
    }

    public boolean deletable(long id) {
        if (securityContainerRepository.existsByParentId(id)) {
            log.info("安全容器[{}]无法被删除, 存在子安全容器", id);
            return false;
        }
        // TODO 判断安全容器下是否存在资源
        return true;
    }

    @Nonnull
    public Set<String> existsResourceTypes(long id) {
        // TODO 获取指定安全容器下拥有的资源类型编码
        return Set.of();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (securityContainerRepository.exists()) {
            return;
        }
        CreateSecurityContainerArgs createSecurityContainerArgs = new CreateSecurityContainerArgs();
        createSecurityContainerArgs.setName("Root Container");
        SecurityContainer container = entityFactory.securityContainer(null, createSecurityContainerArgs, i18nReader);
        securityContainerRepository.insert(container);
    }
}
