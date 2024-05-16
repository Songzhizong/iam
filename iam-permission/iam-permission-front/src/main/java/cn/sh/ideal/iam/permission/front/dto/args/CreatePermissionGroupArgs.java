package cn.sh.ideal.iam.permission.front.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Getter
@Setter
public class CreatePermissionGroupArgs {

    /**
     * 应用ID
     *
     * @required
     */
    @Nullable
    private Long appId;

    /**
     * 分组名称
     *
     * @required
     */
    @Nullable
    private String name;

    /**
     * 排序值
     */
    @Nullable
    private Integer orderNum;

    /**
     * 是否启用,默认是
     */
    @Nullable
    private Boolean enabled;
}
