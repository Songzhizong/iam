package cn.sh.ideal.iam.permission.front.dto.args;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/5/16
 */
@Getter
@Setter
public class CreatePermissionArgs {

    /**
     * 权限项ID
     *
     * @required
     */
    @Nullable
    private Long itemId;

    /**
     * 权限名称
     *
     * @required
     */
    @Nullable
    private String name;

    /** 唯一标识符, 应用下唯一 (可选) */
    @Nullable
    private String ident;

    @Nullable
    private Set<String> apis;

    @Nullable
    private Set<String> authorities;

    @Nullable
    private Set<Long> childIds;

    @Nullable
    private Boolean itemSecurity;

    @Nullable
    private Boolean groupSecurity;

    @Nullable
    private Boolean allInItem;

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
