package cn.sh.ideal.iam.organization.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Getter
@Setter
public class CreateTenantArgs {

    /**
     * 安全容器ID
     */
    @Nullable
    private Long containerId = null;

    /**
     * 租户名称
     *
     * @required
     */
    @Nullable
    private String name = null;

    /**
     * 租户缩写
     *
     * @required
     */
    @Nullable
    private String abbreviation = null;

    /**
     * 描述信息
     */
    @Nullable
    private String note = null;

    /**
     * 系统版本编码
     */
    @Nullable
    private String systemEdition = null;
}
