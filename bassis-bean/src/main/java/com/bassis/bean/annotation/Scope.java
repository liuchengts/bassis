package com.bassis.bean.annotation;

import com.bassis.bean.common.enums.ScopeEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义注入资源注解
 *
 * @see ScopeEnum
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    ScopeEnum value() default ScopeEnum.SINGLETON;
}
