package cn.sh.ideal.iam.infrastructure;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2024/5/31
 */
public class RequestThreadLocalHolder {
    private static final ThreadLocal<Boolean> REQUEST_MARK = new ThreadLocal<>();
    private static final List<ThreadLocal<?>> THREAD_LOCALS = new ArrayList<>();

    public static void register(@Nonnull ThreadLocal<?> threadLocal) {
        THREAD_LOCALS.add(threadLocal);
    }

    public static void clear() {
        REQUEST_MARK.remove();
        for (ThreadLocal<?> threadLocal : THREAD_LOCALS) {
            threadLocal.remove();
        }
    }

    public static void markRequest() {
        REQUEST_MARK.set(Boolean.TRUE);
    }

    public static boolean isRequest() {
        return REQUEST_MARK.get() != null;
    }
}
