package cn.sh.ideal.iam.authorization.standard.dto.args;

import cn.sh.ideal.iam.common.constant.Terminal;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Getter
@Setter
public class CreateAuthClientArgs {

    /**
     * 所属平台
     *
     * @required
     */
    @Nullable
    private String platform;

    /**
     * 客户端名称
     *
     * @required
     */
    @Nullable
    private String name;

    /** 备注信息 */
    @Nullable
    private String note;

    /**
     * 所属终端类型
     *
     * @required
     */
    @Nullable
    private Terminal terminal;
}
