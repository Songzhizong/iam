package cn.sh.ideal.iam.permission.front.port.web;

import cn.idealio.framework.exception.BadRequestException;
import cn.idealio.framework.lang.StringUtils;
import cn.idealio.framework.transmission.Result;
import cn.idealio.framework.util.json.JsonUtils;
import cn.sh.ideal.iam.permission.front.application.AppService;
import cn.sh.ideal.iam.permission.front.domain.model.App;
import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.AppDetail;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
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
 * 前端应用管理
 *
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/front")
public class AppController {
    private final AppService appService;

    /**
     * 新增应用
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/front/apps
     *   Content-Type: application/json
     *
     *   {
     *     "terminal": "WEB",
     *     "rootPath": "/event",
     *     "name": "事件中心",
     *     "orderNum": 0,
     *     "config": "none"
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4q8kana41r7k
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success",
     *     "data": {
     *       "id": "622416657016422400",
     *       "terminal": "WEB",
     *       "rootPath": "/event",
     *       "name": "事件中心",
     *       "orderNum": 0,
     *       "config": "none"
     *     }
     *   }
     * </pre>
     *
     * @author 宋志宗 on 2024/5/16
     */
    @PostMapping("/apps")
    public Result<AppInfo> create(@RequestBody CreateAppArgs args) {
        App app = appService.create(args);
        AppInfo info = app.toInfo();
        return Result.success(info);
    }

    /**
     * 导出应用配置
     *
     * @param appId 应用ID
     * @author 宋志宗 on 2024/5/16
     */
    @GetMapping("/apps/{appId}/export")
    public void exportApp(@PathVariable long appId,
                          @Nonnull HttpServletResponse response) throws IOException {
        AppDetail detail = appService.export(appId);
        String filename = AppService.formatAppConfigName(appId);
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
    @PostMapping("/apps/import")
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
        appService.reload(detail);
        return Result.success();
    }
}
