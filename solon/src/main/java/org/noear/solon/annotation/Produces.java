package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 做为 @Mapping 的副词用，申明输出内容类型
 *
 * @author noear
 * @since 1.7
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Produces {
    String value();
}
