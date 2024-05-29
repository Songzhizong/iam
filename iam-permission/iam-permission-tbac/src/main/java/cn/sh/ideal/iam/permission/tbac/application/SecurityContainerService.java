package cn.sh.ideal.iam.permission.tbac.application;

import cn.idealio.framework.audit.Audits;
import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.lang.TreeNode;
import cn.idealio.framework.lang.Tuple;
import cn.idealio.framework.lock.GlobalLock;
import cn.idealio.framework.lock.GlobalLockFactory;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.infrastructure.configure.IamI18nReader;
import cn.sh.ideal.iam.permission.tbac.application.impl.CachelessTbacHandler;
import cn.sh.ideal.iam.permission.tbac.domain.model.*;
import cn.sh.ideal.iam.permission.tbac.dto.args.CreateSecurityContainerArgs;
import cn.sh.ideal.iam.permission.tbac.dto.resp.SecurityContainerTreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
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
    private final TbacHandler tbacHandler;
    private final IamI18nReader i18nReader;
    private final EntityFactory entityFactory;
    private final GlobalLockFactory globalLockFactory;
    private final CachelessTbacHandler cachelessTbacHandler;
    private final SecurityContainerCache securityContainerCache;
    private final SecurityContainerRepository securityContainerRepository;


    @Transactional(rollbackFor = Throwable.class)
    public SecurityContainer create(@Nonnull CreateSecurityContainerArgs args) {
        Long parentId = args.getParentId();
        SecurityContainer parent = null;
        if (parentId != null) {
            parent = securityContainerRepository.findById(parentId).orElseThrow(() -> {
                log.info("创建安全容器失败, 指定的父容器不存在: {}", parentId);
                String message = i18nReader.getMessage("sc.parent.not_found");
                return new ResourceNotFoundException(message);
            });
        }
        String name = args.getName();
        Asserts.notBlank(name, () -> i18nReader.getMessage("sc.name.blank"));
        if (securityContainerRepository.existsByParentIdAndName(parentId, name)) {
            log.info("创建安全容器失败, 安全容器名称已被使用: [{} {}]", parentId, name);
            throw new BadRequestException(i18nReader.getMessage("sc.name.exists"));
        }
        SecurityContainer securityContainer =
                entityFactory.securityContainer(parent, args, i18nReader);
        return securityContainerRepository.insert(securityContainer);
    }

    @Transactional(rollbackFor = Throwable.class)
    public SecurityContainer rename(long id, @Nullable String name) {
        Asserts.notBlank(name, () -> i18nReader.getMessage("sc.name.blank"));
        SecurityContainer securityContainer =
                securityContainerRepository.requireById(id, i18nReader);
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
        List<SecurityContainer> children =
                securityContainerRepository.findAllByParentIdIn(parentMap.keySet());
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
        SecurityContainer securityContainer =
                securityContainerRepository.requireById(id, i18nReader);
        securityContainerRepository.delete(securityContainer);
        // TODO 发布事件, 删除关联数据
    }

    /**
     * 获取用户所有可见的安全容器树
     *
     * @param userId    用户id
     * @param authority 权限标识
     */
    @Nonnull
    public List<SecurityContainerTreeNode> visibleContainerTree(long userId,
                                                                @Nonnull String authority) {
        // 先刷新安全容器缓存
        securityContainerCache.refresh();

        // 获取用户对应[authority]有直接权限配置的容器分配情况, containerId -> 是否分配 -> 是否继承
        Map<Long, Tuple<Boolean, Boolean>> containerAssignInfo =
                tbacHandler.authorityContainerAssignInfo(userId, authority);
        // 获取所有权限的容器ID列表. 不能走缓存, 因为通过缓存获取的容器ID列表可能不包含最新的
        Set<Long> authorityContainerIds =
                cachelessTbacHandler.analyzeContainerIds(containerAssignInfo);
        if (authorityContainerIds.isEmpty()) {
            return List.of();
        }
        Collection<Object> visibleContainerIds = new HashSet<>(authorityContainerIds);

        Set<Long> containerIds = new HashSet<>();
        containerAssignInfo.forEach((containerId, tuple) -> {
            if (tuple.getFirst()) {
                containerIds.add(containerId);
            }
        });
        List<AnalyzedSecurityContainer> analyzedContainers =
                securityContainerCache.findAllById(containerIds);
        List<AnalyzedSecurityContainer> roots =
                AnalyzedSecurityContainer.filterRoots(analyzedContainers);
        List<SecurityContainer> containers = TreeNode.flatten(roots)
                .stream().map(AnalyzedSecurityContainer::getContainer).toList();
        List<SecurityContainerTreeNode> treeNodeList =
                containers.stream().map(SecurityContainer::toTreeNode).toList();

        return TreeNode.toTreeList(treeNodeList, visibleContainerIds);
    }

    /** 获取用户在指定安全容器之上的所有可见父容器 */
    @Nonnull
    public List<SecurityContainerTreeNode> visibleContainerParentTree(long userId,
                                                                      long containerId,
                                                                      @Nullable String authority) {
        SecurityContainer container =
                securityContainerRepository.requireById(containerId, i18nReader);
        SecurityContainerTreeNode node = container.toTreeNode();
        SequencedSet<Long> parentIds = container.parentIds();
        if (parentIds.isEmpty()) {
            return List.of(node);
        }

        // 获取从根节点到指定节点的全量节点列表
        List<SecurityContainer> parents = securityContainerRepository.findAllById(parentIds);
        List<SecurityContainerTreeNode> allNodes = new ArrayList<>(parents.size() + 1);
        for (SecurityContainer parent : parents) {
            SecurityContainerTreeNode parentNode = parent.toTreeNode();
            allNodes.add(parentNode);
        }
        allNodes.add(node);

        if (StringUtils.isBlank(authority)) {
            return TreeNode.toTreeList(allNodes);
        }

        // 获取所有权限的容器ID列表. 不能走缓存, 因为通过缓存获取的容器ID列表可能不包含最新的
        Set<Long> assignedContainerIds =
                cachelessTbacHandler.authorityContainerIds(userId, authority);
        if (assignedContainerIds.isEmpty()) {
            return List.of(node);
        }
        Collection<Object> retainContainerIds = new HashSet<>(assignedContainerIds);
        if (!assignedContainerIds.contains(containerId)) {
            retainContainerIds.add(containerId);
        }

        return TreeNode.toTreeList(allNodes, retainContainerIds);
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
        SecurityContainer container =
                entityFactory.securityContainer(createSecurityContainerArgs, i18nReader);
        securityContainerRepository.insert(container);
        log.info("表里没有任何安全容器信息, 初始化根安全容器");
    }
}
