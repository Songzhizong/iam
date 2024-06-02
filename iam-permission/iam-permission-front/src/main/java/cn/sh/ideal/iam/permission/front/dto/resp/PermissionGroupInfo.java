package cn.sh.ideal.iam.permission.front.dto.resp;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/5/30
 */
@Getter
@Setter
public class PermissionGroupInfo {

    /** 主键 */
    @Nonnull
    private Long id = -1L;

    /** 应用ID */
    @Nonnull
    private Long appId = -1L;

    /** 名称 */
    private String name = "";

    /** 是否启用 */
    private boolean enabled = true;

    /** 排序值 */
    private int orderNum = 0;
}
