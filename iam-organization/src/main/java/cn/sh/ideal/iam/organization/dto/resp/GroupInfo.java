package cn.sh.ideal.iam.organization.dto.resp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 宋志宗 on 2024/5/15
 */
@Getter
@Setter
public class GroupInfo {
    /** 用户组ID */
    private Long id;

    /** 所属租户ID */
    private Long tenantId;

    /** 所属安全容器ID */
    private Long containerId;

    /** 用户组名称 */
    private String name;

    /** 备注信息 */
    private String note;

}
