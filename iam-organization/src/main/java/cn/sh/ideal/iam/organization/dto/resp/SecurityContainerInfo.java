package cn.sh.ideal.iam.organization.dto.resp;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2024/5/14
 */
@Getter
@Setter
public class SecurityContainerInfo {
    @Nullable
    private Long id;

    @Nullable
    private Long parentId;

    @Nullable
    private String name;
}
