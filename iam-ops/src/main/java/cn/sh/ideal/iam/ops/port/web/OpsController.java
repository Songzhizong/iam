package cn.sh.ideal.iam.ops.port.web;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.transmission.Result;
import cn.idealio.framework.util.json.JsonUtils;
import cn.sh.ideal.iam.ops.application.AppOpsService;
import cn.sh.ideal.iam.ops.application.ConfigOpsService;
import cn.sh.ideal.iam.ops.application.PlatformOpsService;
import cn.sh.ideal.iam.ops.dto.resp.PlatformDetail;
import cn.sh.ideal.iam.permission.front.dto.resp.AppDetail;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/ops")
public class OpsController {
    private final AppOpsService appOpsService;
    private final ConfigOpsService configOpsService;
    private final PlatformOpsService platformOpsService;

    /**
     * 导出平台配置
     *
     * @param code 平台编码
     * @author 宋志宗 on 2024/6/2
     */
    @GetMapping("/platforms/{code}/export")
    public void exportPlatform(@PathVariable String code,
                               @Nonnull HttpServletResponse response) throws IOException {
        PlatformDetail detail = platformOpsService.export(code);
        String filename = PlatformOpsService.formatPlatformConfigName(code);
        String name = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        String jsonString = JsonUtils.toJsonString(detail);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + name);
        response.setCharacterEncoding("UTF-8");
        log.info("导出平台[{}]: {}", code, jsonString);
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        response.setContentLength(bytes.length);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            IOUtils.copyLarge(inputStream, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    /**
     * 导入平台配置
     *
     * @param file 配置文件
     * @author 宋志宗 on 2024/6/2
     */
    @PostMapping("/platforms/import")
    public Result<Void> importPlatform(@Nonnull @RequestParam("file")
                                       MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (StringUtils.isNotBlank(name) && !name.endsWith(".json")) {
            log.info("导入平台配置文件类型非法: {}", name);
            throw new BadRequestException("文件类型非法");
        }
        byte[] bytes = file.getBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        PlatformDetail detail = JsonUtils.parse(json, PlatformDetail.class);
        platformOpsService.reload(detail);
        return Result.success();
    }

    /**
     * 导出应用配置
     *
     * @param appId 应用ID
     * @author 宋志宗 on 2024/5/16
     */
    @GetMapping("/front/apps/{appId}/export")
    public void exportApp(@PathVariable @Nonnull Long appId,
                          @Nonnull HttpServletResponse response) throws IOException {
        AppDetail detail = appOpsService.export(appId);
        String filename = AppOpsService.formatAppConfigName(appId);
        String name = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        String jsonString = JsonUtils.toJsonString(detail);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + name);
        response.setCharacterEncoding("UTF-8");
        log.info("导出应用[{}]: {}", appId, jsonString);
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        response.setContentLength(bytes.length);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            IOUtils.copyLarge(inputStream, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    /**
     * 导入应用配置
     *
     * @param file 文件
     */
    @PostMapping("/front/apps/import")
    public Result<Void> importApp(@Nonnull @RequestParam("file")
                                  MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (StringUtils.isNotBlank(name) && !name.endsWith(".json")) {
            log.info("导入应用配置文件类型非法: {}", name);
            throw new BadRequestException("文件类型非法");
        }
        byte[] bytes = file.getBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        AppDetail detail = JsonUtils.parse(json, AppDetail.class);
        appOpsService.reload(detail);
        return Result.success();
    }

    /**
     * 重新导入前端应用
     *
     * @author 宋志宗 on 2024/6/2
     */
    @PostMapping("/front/apps/reimport")
    public Result<Void> reloadFrontApps() {
        String backupDirName = configOpsService.generateBackupDirName();
        configOpsService.reloadApps(ConfigOpsService.IMPORT_APP_PATH, backupDirName);
        return Result.success();
    }
}
