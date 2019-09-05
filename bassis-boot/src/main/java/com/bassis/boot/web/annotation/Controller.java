package com.bassis.boot.web.annotation;

import com.bassis.bean.annotation.Component;

import java.lang.annotation.*;

/**
 * 自定义action代理注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@Component
public @interface Controller {
    String value() default "";
}
