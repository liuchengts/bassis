package com.bassis.bean.annotation;

import com.bassis.bean.common.enums.ScopeEnum;

import java.lang.annotation.*;

/**
 * bean生成范围
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Scope {
    /**
     * 当前bean的生成范围
     * SINGLETON 表示为单例模式，即整个beanfactory周期内只会存在一个bean
     * PROTOTYPE 表示为多例模式，即整个beanfactory周期内每次需要都会创建一个新的bean
     * 默认为 SINGLETON
     *
     * @see ScopeEnum
     */
    ScopeEnum value() default ScopeEnum.SINGLETON;
}
