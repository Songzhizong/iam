package cn.sh.ideal.iam.ops.application;

import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.lock.GlobalLock;
import cn.idealio.framework.lock.GlobalLockFactory;
import cn.idealio.framework.util.json.JsonUtils;
import cn.idealio.framework.util.time.DateTimes;
import cn.sh.ideal.iam.permission.front.dto.resp.AppDetail;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigOpsService {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String TIME_FORMAT_PATTERN = "yyMMdd_HHmmss";
    public static final String OPS_BASE_PATH = "config/ops";
    public static final String BACK_PATH = "config/backup/ops";
    public static final String IMPORT_APP_PATH = OPS_BASE_PATH + "/import_front_apps";
    public static final String IMPORT_PLATFORM_PATH = OPS_BASE_PATH + "/import_platforms";
    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(30);
    private final String lockCertificate = UUID.randomUUID().toString();

    private final AppOpsService appOpsService;
    private final GlobalLockFactory globalLockFactory;

    @Nonnull
    public String generateBackupDirName() {
        LocalDateTime time = DateTimes.now();
        return DateTimes.format(time, TIME_FORMAT_PATTERN);
    }

    @Nonnull
    public String formatAppBackupDirPath(@Nonnull String backupDirName) {
        return BACK_PATH + "/" + backupDirName + "/front_apps";
    }

    public void backupApp(@Nonnull Long appId, @Nonnull String backupDirName) throws IOException {
        AppDetail detail = appOpsService.export(appId);
        String jsonString = JsonUtils.toJsonString(detail);
        String appBackupDirPath = formatAppBackupDirPath(backupDirName);
        File backupDir = new File(appBackupDirPath);
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            backupDir.mkdirs();
        }
        String fileName = "app_config_" + appId + ".json";
        File file = new File(backupDir, fileName);
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonString);
            fileWriter.flush();
            log.info("成功备份应用[{}]到: {}", appId, file.getAbsolutePath());
        }
    }

    /**
     * 重新加载应用配置
     *
     * @param path 应用配置目录路径
     * @author 宋志宗 on 2024/6/2
     */
    @Transactional(rollbackFor = Throwable.class)
    public void reloadApps(@Nonnull String path, @Nullable String backupDirName) {
        String lockKey = "iam:ops:reload_apps";
        GlobalLock lock = globalLockFactory.getLock(lockKey, LOCK_TIMEOUT);
        if (!lock.tryLock(lockCertificate)) {
            log.warn("reload apps is locked");
            return;
        }
        try {
            File configDir = new File(path);
            if (!configDir.exists() || !configDir.isDirectory()) {
                log.error("App config dir not exists: {}", path);
                return;
            }
            File[] files = configDir.listFiles();
            if (files == null || files.length == 0) {
                log.warn("App config dir is empty: {}", path);
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                }
                String name = file.getName();
                if (!name.endsWith(".json")) {
                    log.info("跳过非json文件: {}/{}", path, name);
                    continue;
                }
                log.info("reload app config: {}/{}", path, name);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    AppDetail detail = JsonUtils.parse(fileInputStream, AppDetail.class);
                    AppInfo app = detail.getApp();
                    if (app == null) {
                        log.error("App info not found in file: {}/{}", path, name);
                        continue;
                    }
                    Long id = app.getId();
                    if (id < 1) {
                        log.error("App id is invalid: {}/{}", path, name);
                        continue;
                    }
                    // 备份原有的应用配置信息
                    if (StringUtils.isNotBlank(backupDirName)) {
                        backupApp(id, backupDirName);
                    }
                    appOpsService.reload(detail);
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                } catch (IOException e) {
                    log.warn("reload app config failure: {}/{}", path, name, e);
                    throw new RuntimeException(e);
                }
            }
        } finally {
            lock.unlock(lockCertificate);
        }
    }
}
