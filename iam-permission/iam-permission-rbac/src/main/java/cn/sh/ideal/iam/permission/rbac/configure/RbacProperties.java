package cn.sh.ideal.iam.permission.rbac.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 宋志宗 on 2023/12/28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ideal-iam.permission.rbac")
public class RbacProperties {

    /** 是否启用权限缓存, 默认是 */
    private boolean enableCache = true;

}
