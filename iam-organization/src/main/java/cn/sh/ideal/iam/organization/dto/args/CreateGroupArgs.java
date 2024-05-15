package cn.sh.ideal.iam.organization.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/15
 */
@Getter
@Setter
public class CreateGroupArgs {

    /** 所属安全容器ID */
    @Nullable
    private Long containerId;

    /**
     * 用户组名称
     *
     * @required
     */
    @Nullable
    private String name;

    /** 用户组备注 */
    @Nullable
    private String note;
}
