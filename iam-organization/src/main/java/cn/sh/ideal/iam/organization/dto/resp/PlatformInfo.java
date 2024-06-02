package cn.sh.ideal.iam.organization.dto.resp;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Getter
@Setter
public class PlatformInfo {

    @Nonnull
    private Long id;

    @Nonnull
    private String code = "";

    @Nonnull
    private String name = "";

    @Nonnull
    private String openName = "";

    @Nonnull
    private String note = "";

    private boolean registrable = false;
}
