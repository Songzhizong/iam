package cn.sh.ideal.iam.authorization.standard.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
}
