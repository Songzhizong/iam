package cn.sh.ideal.iam.permission.front.dto.args;

import cn.sh.ideal.iam.common.constant.Terminal;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class CreateAppArgs {
    /**
     * 终端类型
     *
     * @required
     */
    @Nullable
    private Terminal terminal;

    /**
     * 跟路径, 同终端下全局唯一
     *
     * @required
     */
    @Nullable
    private String rootPath;

    /**
     * 名称
     *
     * @required
     */
    @Nullable
    private String name;

    /** 备注信息 */
    @Nullable
    private String note;

    /** 排序值 */
    @Nullable
    private Integer orderNum;

    /** 配置信息 */
    @Nullable
    private String config;
}
