package com.bassis.bean.annotation;

import java.lang.annotation.*;

/**
 * 自定义bean注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Component {
    /**
     * bean对象的别名
     */
    String name() default "";
}
