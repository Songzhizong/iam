package cn.sh.ideal.iam.permission.front.port.web;

import cn.idealio.framework.transmission.Result;
import cn.sh.ideal.iam.permission.front.application.AppService;
import cn.sh.ideal.iam.permission.front.domain.model.App;
import cn.sh.ideal.iam.permission.front.dto.args.CreateAppArgs;
import cn.sh.ideal.iam.permission.front.dto.resp.AppInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端应用管理
 *
 * @author 宋志宗 on 2024/2/5
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam/front")
public class AppController {
    private final AppService appService;

    /**
     * 新增应用
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
