package com.bassis.bean.annotation;

import com.bassis.bean.common.enums.ScopeEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * bean生成范围
 * 单例模式
 * 多例模式 BeanFactory.createBean()下每次都会生成一个新的bean对象
 *
 * @see ScopeEnum
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    ScopeEnum value() default ScopeEnum.SINGLETON;
}
