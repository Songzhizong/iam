package cn.sh.ideal.iam.ops.application;

import cn.idealio.framework.exception.ResourceNotFoundException;
import cn.idealio.framework.util.Asserts;
import cn.sh.ideal.iam.permission.front.domain.model.*;
import cn.sh.ideal.iam.permission.front.dto.resp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppOpsService {
    private final EntityFactory entityFactory;
    private final AppRepository appRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionItemRepository permissionItemRepository;
    private final PermissionGroupRepository permissionGroupRepository;

    @Nonnull
    public static String formatAppConfigName(@Nonnull Long appId) {
        return "app_config_" + appId + ".json";
    }

    @Nonnull
    public AppDetail export(@Nonnull Long appId) throws ResourceNotFoundException {
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

    @Transactional(rollbackFor = Throwable.class)
    public void reload(@Nonnull AppDetail appDetail) {
        AppInfo appInfo = appDetail.getApp();
        Asserts.nonnull(appInfo, "应用信息为空");
        // 清空之前的应用数据
        Long appId = appInfo.getId();
        appRepository.findById(appId).ifPresent(appRepository::delete);
        int pc = permissionRepository.deleteAllByAppId(appId);
        int pic = permissionItemRepository.deleteAllByAppId(appId);
        int pgc = permissionGroupRepository.deleteAllByAppId(appId);
        log.info("应用[{}]删除成功, 删除权限: {}条, 权限项: {}条, 权限组: {}条", appId, pc, pic, pgc);

        List<PermissionInfo> permissionInfos = appDetail.getPermissions();
        List<PermissionItemInfo> permissionItemInfos = appDetail.getPermissionItems();
        List<PermissionGroupInfo> permissionGroupInfos = appDetail.getPermissionGroups();

        App app = entityFactory.app(appInfo);
        List<Permission> permissions = permissionInfos.stream().map(entityFactory::permission).toList();
        List<PermissionItem> permissionItems = permissionItemInfos.stream().map(entityFactory::permissionItem).toList();
        List<PermissionGroup> permissionGroups = permissionGroupInfos.stream().map(entityFactory::permissionGroup).toList();

        appRepository.insert(app);
        permissionItemRepository.insert(permissionItems);
        permissionGroupRepository.insert(permissionGroups);
        permissionRepository.insert(permissions);
        log.info("应用[{}]导入成功, 导入权限: {}条, 权限项: {}条, 权限组: {}条",
                appId, permissions.size(), permissionItems.size(), permissionGroups.size());
    }
}
