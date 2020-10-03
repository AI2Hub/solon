package org.noear.solon.extend.socketapi;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XServerEndpoint {
    String value() default "";
}
