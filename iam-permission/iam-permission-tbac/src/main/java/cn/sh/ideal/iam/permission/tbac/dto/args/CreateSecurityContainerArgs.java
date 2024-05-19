package cn.sh.ideal.iam.permission.tbac.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Getter
@Setter
public class CreateSecurityContainerArgs {

    /**
     * 父节点ID
     *
     * @required
     */
    @Nullable
    private Long parentId = null;

    /**
     * 名称
     *
     * @required
     */
    @Nullable
    private String name = null;
}
