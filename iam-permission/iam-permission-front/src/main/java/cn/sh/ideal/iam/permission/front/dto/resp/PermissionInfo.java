package cn.sh.ideal.iam.permission.front.dto.resp;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class PermissionInfo {
    /** 主键 */
    private long id = -1L;

    /** 应用ID */
    private long appId = -1L;

    /** 权限组ID */
    private long groupId = -1L;

    /** 权限项ID */
    private long itemId = -1L;

    /** 名称 */
    private String name = "";

    /** API列表 */
    private Set<String> apis = new LinkedHashSet<>();

    /** 权限标识列表 */
    private Set<String> authorities = new LinkedHashSet<>();

    /** 子权限ID列表 */
    private Set<Long> childIds = new LinkedHashSet<>();

    /** 是否拥有所在配置项安全权限 */
    private boolean itemSecurity = false;

    /** 是否拥有所在配置组安全权限 */
    private boolean groupSecurity = false;

    /** 是否拥有配置项内所有权限 */
    private boolean allInItem = false;

    /** 是否启用 */
    private boolean enabled = true;

    /** 排序值 */
    private int orderNum = 0;
}
