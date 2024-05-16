package cn.sh.ideal.iam.permission.front.application;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.core.constant.Terminal;
import cn.sh.ideal.iam.permission.front.domain.model.*;
import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppService {
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
        App app = entityFactory.app(args);
        return appRepository.insert(app);
    }

    @Nullable
    @Transactional(rollbackFor = Throwable.class)
    public App delete(long id) {
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

    @Nonnull
    public AppDetail export(long appId) {
        App app = appRepository.findById(appId).orElseThrow(() -> {
            log.info("导出应用配置失败, 应用不存在: {}", appId);
            return new ResourceNotFoundException("应用不存在");
        });
        List<Permission> permissions = permissionRepository.findAllByAppId(appId);
        List<PermissionItem> permissionItems = permissionItemRepository.findAllByAppId(appId);
        List<PermissionGroup> permissionGroups = permissionGroupRepository.findAllByAppId(appId);

        AppInfo appInfo = app.toInfo();
        List<PermissionInfo> permissionInfos = permissions.stream().map(Permission::toInfo).toList();
        List<PermissionItemInfo> permissionItemInfos = permissionItems.stream().map(PermissionItem::toInfo).toList();
        List<PermissionGroupInfo> permissionGroupInfos = permissionGroups.stream().map(PermissionGroup::toInfo).toList();
        return new AppDetail(appInfo, permissionInfos, permissionItemInfos, permissionGroupInfos);
    }

    public void reload(@Nonnull AppDetail appDetail) {
        AppInfo appInfo = appDetail.getApp();
        Asserts.nonnull(appInfo, "应用信息为空");
        // 清空之前的应用数据
        long appId = appInfo.getId();
        appRepository.findById(appId).ifPresent(appRepository::delete);
        long pc = permissionRepository.deleteAllByAppId(appId);
        long pic = permissionItemRepository.deleteAllByAppId(appId);
        long pgc = permissionGroupRepository.deleteAllByAppId(appId);
        log.info("应用[{}]删除成功, 删除权限: {}条, 权限项: {}条, 权限组: {}条", appId, pc, pic, pgc);

        List<PermissionInfo> permissionInfos = appDetail.getPermissions();
        List<PermissionItemInfo> permissionItemInfos = appDetail.getPermissionItems();
        List<PermissionGroupInfo> permissionGroupInfos = appDetail.getPermissionGroups();

        App app = entityFactory.app(appInfo);
        List<Permission> permissions = permissionInfos.stream().map(entityFactory::permission).toList();
        List<PermissionItem> permissionItems = permissionItemInfos.stream().map(entityFactory::permissionItem).toList();
        List<PermissionGroup> permissionGroups = permissionGroupInfos.stream().map(entityFactory::permissionGroup).toList();

        appRepository.insert(app);
        permissionRepository.insert(permissions);
        permissionItemRepository.insert(permissionItems);
        permissionGroupRepository.insert(permissionGroups);
        log.info("应用[{}]导入成功, 导入权限: {}条, 权限项: {}条, 权限组: {}条",
                appId, permissions.size(), permissionItems.size(), permissionGroups.size());
    }
}
