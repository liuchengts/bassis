package com.bassis.boot.web.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface RequestParam {
    /**
     * 参数名称
     */
    String name() default "";

    /**
     * 是否必须 默认是必须的
     */
    boolean required() default true;
}
