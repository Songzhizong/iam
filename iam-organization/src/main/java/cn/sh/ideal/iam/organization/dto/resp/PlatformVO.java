package cn.sh.ideal.iam.organization.dto.resp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class PlatformVO {
    /** 平台编码 */
    private String code;

    /** 平台名称 */
    private String name = "";

    /** 对外显示名称 */
    private String openName = "";

    /** 备注信息 */
    private String note = "";

    /** 是否允许注册 */
    private boolean registrable = false;

    /** 配置信息 */
    private String config = "";
}
