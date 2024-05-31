package cn.sh.ideal.iam.security.api;

import cn.idealio.framework.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author 宋志宗 on 2024/2/5
 */
@Slf4j
public class SecurityContextHolder {
    private static final ThreadLocal<SecurityContext> SECURITY_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取当前权限上下文
     *
     * @return 当前权限上下文, 如果获取失败则抛出UnauthorizedException
     */
    @Nonnull
    public static SecurityContext get() throws UnauthorizedException {
        SecurityContext securityContext = SECURITY_CONTEXT_THREAD_LOCAL.get();
        if (securityContext == null) {
            log.info("当前线程没有权限上下文");
            throw new UnauthorizedException("Unauthorized");
        }
        return securityContext;
    }

    /**
     * 尝试获取当前的权限上下文
     * <p>
     * 如果为空则返回{@link Optional#empty()}
     *
     * @return 当前权限上下文
     */
    @Nonnull
    public static Optional<SecurityContext> optional() {
        return Optional.ofNullable(SECURITY_CONTEXT_THREAD_LOCAL.get());
    }

    public static void clear() {
        SECURITY_CONTEXT_THREAD_LOCAL.remove();
    }

    public static void setContext(@Nonnull SecurityContext securityContext) {
        SECURITY_CONTEXT_THREAD_LOCAL.set(securityContext);
    }
}
