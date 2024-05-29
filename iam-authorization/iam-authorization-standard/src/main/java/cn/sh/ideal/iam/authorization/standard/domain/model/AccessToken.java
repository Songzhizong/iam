package cn.sh.ideal.iam.authorization.standard.domain.model;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AccessToken {

    Long getId();

    long getUserId();

    long getTenantId();

}
