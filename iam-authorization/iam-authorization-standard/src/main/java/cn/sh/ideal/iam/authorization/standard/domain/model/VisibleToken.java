package cn.sh.ideal.iam.authorization.standard.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Getter
@Setter
public class VisibleToken {

    /** token类型 */
    @JsonProperty("token_type")
    private String tokenType;

    /** token值 */
    @JsonProperty("access_token")
    private String accessToken;

    @Nonnull
    public static VisibleToken create(@Nonnull String type,
                                      @Nonnull String visibleToken) {
        VisibleToken token = new VisibleToken();
        token.setTokenType(type);
        token.setAccessToken(visibleToken);
        return token;
    }
}
