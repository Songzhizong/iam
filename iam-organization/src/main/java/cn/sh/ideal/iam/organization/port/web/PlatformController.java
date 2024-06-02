package cn.sh.ideal.iam.organization.port.web;

import cn.idealio.framework.transmission.ListResult;
import cn.idealio.framework.transmission.Result;
import cn.sh.ideal.iam.organization.application.PlatformService;
import cn.sh.ideal.iam.organization.domain.model.Platform;
import cn.sh.ideal.iam.organization.dto.args.CreatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.args.UpdatePlatformArgs;
import cn.sh.ideal.iam.organization.dto.resp.PlatformVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台管理
 *
 * @author 宋志宗 on 2024/5/16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/iam")
public class PlatformController {
    private final PlatformService platformService;

    /**
     * 创建平台
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *   POST {{base_url}}/iam/platforms
     *   Content-Type: application/json
     *
     *   {
     *     "code": "customer",
     *     "name": "客户平台",
     *     "openName": "苏州算力平台",
     *     "note": "苏州算力平台",
     *     "registrable": true,
     *     "config": "{}"
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4q1bract0w
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success",
     *     "data": {
     *       "code": "customer",
     *       "name": "客户平台",
     *       "openName": "苏州算力平台",
     *       "note": "苏州算力平台",
     *       "registrable": true,
     *       "config": "{}"
     *     }
     *   }
     * </pre>
     *
     * @author 宋志宗 on 2024/5/28
     */
    @PostMapping("/platforms")
    public Result<PlatformVO> create(@RequestBody CreatePlatformArgs args) {
        Platform platform = platformService.create(args);
        PlatformVO vo = platform.toVO();
        return Result.success(vo);
    }

    /**
     * 更新平台信息
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *   PUT {{base_url}}/iam/platforms/customer
     *   Content-Type: application/json
     *
     *   {
     *     "name": "客户平台",
     *     "openName": "苏州算力平台",
     *     "note": "苏州算力平台1",
     *     "registrable": false,
     *     "config": "{}"
     *   }
     *
     *   <p><b>响应示例</b></p>
     *   见 {@link #create(CreatePlatformArgs)}
     * </pre>
     *
     * @param code 平台编码
     * @author 宋志宗 on 2024/5/28
     */
    @PutMapping("/platforms/{code}")
    public Result<PlatformVO> update(@PathVariable String code,
                                     @RequestBody UpdatePlatformArgs args) {
        Platform platform = platformService.update(code, args);
        PlatformVO vo = platform.toVO();
        return Result.success(vo);
    }

    /**
     * 删除平台
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *   DELETE {{base_url}}/iam/platforms/customer
     *
     *   <p><b>响应示例</b></p>
     *   见 {@link #create(CreatePlatformArgs)}
     * </pre>
     *
     * @param code 平台编码
     * @author 宋志宗 on 2024/5/28
     */
    @DeleteMapping("/platforms/{code}")
    public Result<PlatformVO> delete(@PathVariable String code) {
        Platform platform = platformService.delete(code);
        if (platform == null) {
            return Result.success();
        }
        PlatformVO vo = platform.toVO();
        return Result.success(vo);
    }

    /**
     * 获取所有平台列表
     * <pre>
     *   <b>需要权限: 无</b>
     *   <p><b>请求示例</b></p>
     *   GET {{base_url}}/iam/platforms
     *
     *   <p><b>响应示例</b></p>
     *   HTTP/1.1 200
     *   x-ideal-trace-id: 4q1bract0w
     *   Content-Type: application/json
     *
     *   {
     *     "success": true,
     *     "message": "success",
     *     "data": [
     *       {
     *         "code": "customer",
     *         "name": "客户平台",
     *         "openName": "苏州算力平台",
     *         "note": "苏州算力平台",
     *         "registrable": true,
     *         "config": "{}"
     *       }
     *     ]
     *   }
     * </pre>
     *
     * @author 宋志宗 on 2024/5/28
     */
    @GetMapping("/platforms")
    public ListResult<PlatformVO> findALl() {
        List<Platform> platforms = platformService.findAll();
        List<PlatformVO> list = platforms.stream().map(Platform::toVO).toList();
        return ListResult.of(list);
    }
}
