package cn.sh.ideal.iam.ops.application;

import cn.sh.ideal.iam.organization.domain.model.PlatformRepository;
import cn.sh.ideal.iam.permission.front.domain.model.AppRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitializationService implements ApplicationRunner {
    private final AppRepository appRepository;
    private final ConfigOpsService configOpsService;
    private final PlatformRepository platformRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (appRepository.exists() || platformRepository.exists()) {
            return;
        }
        // 载入应用配置
        log.info("initialize iam...");
        configOpsService.reloadApps(ConfigOpsService.IMPORT_APP_PATH, null);

        // 载入平台配置

        // 为各平台创建默认租户、用户、用户组
    }
}
