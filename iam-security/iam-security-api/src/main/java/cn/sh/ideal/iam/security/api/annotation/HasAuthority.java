package cn.sh.ideal.iam.security.api.annotation;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * 用于指定需要什么样的权限
 *
 * @author 宋志宗 on 2023/2/28
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasAuthority {

    @Nonnull
    String value();
}
