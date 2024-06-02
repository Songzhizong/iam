package cn.sh.ideal.iam.permission.front.application;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.common.constant.Terminal;
import cn.sh.ideal.iam.infrastructure.configure.IamIDGenerator;
import cn.sh.ideal.iam.permission.front.domain.model.*;
import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
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
public class AppService {
    private final IamIDGenerator idGenerator;
    private final AppRepository appRepository;
    private final EntityFactory entityFactory;
    private final PermissionRepository permissionRepository;
    private final PermissionItemRepository permissionItemRepository;
    private final PermissionGroupRepository permissionGroupRepository;

    @Nonnull
    @Transactional(rollbackFor = Throwable.class)
    public App create(@Nonnull CreateAppArgs args) {
        Terminal terminal = args.getTerminal();
        String rootPath = args.getRootPath();
        Asserts.nonnull(terminal, "终端类型为空");
        Asserts.notBlank(rootPath, "应用根路径为空");
        if (appRepository.existsByTerminalAndRootPath(terminal, rootPath)) {
            log.error("创建应用失败, 终端[{}]下已存在根路径: [{}]", terminal, rootPath);
            throw new BadRequestException("终端下已存在此根路径");
        }
        long id = idGenerator.generate();
        App app = entityFactory.app(id, args);
        return appRepository.insert(app);
    }

    @Nullable
    @Transactional(rollbackFor = Throwable.class)
    public App delete(@Nonnull Long id) {
        App app = appRepository.findById(id).orElse(null);
        if (app == null) {
            log.info("删除的应用不存在: {}", id);
            return null;
        }
        if (permissionRepository.existsByAppId(id)) {
            log.info("删除应用[{}]失败, 存在权限数据", id);
            throw new BadRequestException("存在权限数据");
        }
        if (permissionItemRepository.existsByAppId(id)) {
            log.info("删除应用[{}]失败, 存在权限项数据", id);
            throw new BadRequestException("存在权限项数据");
        }
        if (permissionGroupRepository.existsByAppId(id)) {
            log.info("删除应用[{}]失败, 存在权限组数据", id);
            throw new BadRequestException("存在权限组数据");
        }
        appRepository.delete(app);
        return app;
    }

    @Nonnull
    public List<App> findAll() {
        return appRepository.findAll();
    }
}
