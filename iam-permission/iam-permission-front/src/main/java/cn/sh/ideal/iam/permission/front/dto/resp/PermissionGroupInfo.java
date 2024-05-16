package cn.sh.ideal.iam.permission.front.dto.resp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class PermissionGroupInfo {

    /** 主键 */
    private long id = -1L;

    /** 应用ID */
    private long appId = -1L;

    /** 名称 */
    private String name = "";

    /** 是否启用 */
    private boolean enabled = true;

    /** 排序值 */
    private int orderNum = 0;
}
