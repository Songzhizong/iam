package cn.sh.ideal.iam.permission.tbac.dto.resp;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Getter
@Setter
public class SecurityContainerInfo {
    /** 安全容器ID */
    @Nullable
    private Long id;

    /** 父容器ID */
    @Nullable
    private Long parentId;

    /** 容器名称 */
    @Nullable
    private String name;
}
