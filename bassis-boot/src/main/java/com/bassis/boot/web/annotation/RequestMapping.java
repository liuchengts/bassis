package com.bassis.boot.web.annotation;

import com.bassis.boot.web.common.enums.RequestMethodEnum;

import java.lang.annotation.*;

/**
 * 寻址
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RequestMapping {
    /**
     * 请求路径
     */
    String value() default "";

    /**
     * 请求方式
     */
    RequestMethodEnum[] method() default {};
}
