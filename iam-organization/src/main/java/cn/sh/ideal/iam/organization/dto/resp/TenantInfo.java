package cn.sh.ideal.iam.organization.dto.resp;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Getter
@Setter
public class TenantInfo {

    /** 主键 */
    private long id = -1L;

    /** 安全容器ID */
    @Nullable
    private Long containerId;

    /** 名称 */
    @Nullable
    private String name = "";

    /** 缩写 */
    @Nullable
    private String abbreviation = "";

    /** 备注 */
    @Nullable
    private String note = "";

    /** 系统版本编码 */
    @Nullable
    private String systemEdition = "";

}
