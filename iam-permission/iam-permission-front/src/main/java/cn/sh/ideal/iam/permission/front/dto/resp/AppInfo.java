package cn.sh.ideal.iam.permission.front.dto.resp;

import cn.sh.ideal.iam.core.constant.Terminal;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class AppInfo {
    /** 主键 */
    private long id = -1L;

    /** 终端类型 */
    @Nonnull
    private Terminal terminal = Terminal.WEB;

    /** 跟路径, 同终端下全局唯一 */
    @Nonnull
    private String rootPath = "";

    /** 名称 */
    @Nonnull
    private String name = "";

    /** 备注 */
    @Nonnull
    private String note = "";

    /** 排序值 */
    private int orderNum = 0;

    /** 配置信息 */
    @Nonnull
    private String config = "";
}
