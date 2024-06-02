package cn.sh.ideal.iam.organization.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Getter
@Setter
public class CreatePlatformArgs {

    /**
     * 平台唯一编码
     *
     * @required
     */
    @Nullable
    private String code;

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
}
