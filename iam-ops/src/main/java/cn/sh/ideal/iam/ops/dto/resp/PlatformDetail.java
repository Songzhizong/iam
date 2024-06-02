package cn.sh.ideal.iam.ops.dto.resp;

import cn.sh.ideal.iam.authorization.standard.domain.model.AuthClientInfo;
import cn.sh.ideal.iam.organization.dto.resp.PlatformInfo;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2024/6/2
 */
@Getter
@Setter
public class PlatformDetail {
    @Nullable
    private PlatformInfo platform;

    @Nonnull
    private List<AuthClientInfo> authClients = new ArrayList<>();

    public PlatformDetail() {
    }

    public PlatformDetail(@Nullable PlatformInfo platform,
                          @Nonnull List<AuthClientInfo> authClients) {
        this.platform = platform;
        this.authClients = authClients;
    }
}
