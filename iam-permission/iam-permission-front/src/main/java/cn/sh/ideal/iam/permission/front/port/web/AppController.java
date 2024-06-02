package cn.sh.ideal.iam.permission.front.port.web;

import cn.idealio.framework.transmission.Result;
import cn.sh.ideal.iam.permission.front.application.AppService;
import cn.sh.ideal.iam.permission.front.domain.model.App;
import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端应用管理
 *
 * @author 宋志宗 on 2024/5/16
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
}
