package cn.sh.ideal.iam.organization.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class UpdatePlatformArgs {

    /**
     * 平台名称
     *
     * @required
     */
    @Nullable
    private String name;

    /** 平台对外显示名称 */
    @Nullable
    private String openName;

    /** 平台备注信息 */
    @Nullable
    private String note;

    /** 是否允许自主注册账号, 默认否 */
    @Nullable
    private Boolean registrable;

    /** 平台前端配置信息 */
    @Nullable
    private String config;
}
