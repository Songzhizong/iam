package cn.sh.ideal.iam.authorization.standard.domain.model;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2024/2/5
 */
public interface AccessToken {

    Long getId();

    long getUserId();

    @Nonnull
    Long getTenantId();

    long getSessionTimeout();

    long getExpiration();

    void setExpiration(long expiration);

    long getLatestActivity();

    void setLatestActivity(long latestActivity);

    default boolean renewal() {
        boolean changed = false;
        long currentTimeMillis = System.currentTimeMillis();
        long latestActivity = getLatestActivity();
        if (currentTimeMillis - latestActivity > 60_000) {
            changed = true;
            setLatestActivity(currentTimeMillis);
            long sessionTimeout = getSessionTimeout();
            setExpiration(currentTimeMillis + sessionTimeout);
        }
        return changed;
    }
}
