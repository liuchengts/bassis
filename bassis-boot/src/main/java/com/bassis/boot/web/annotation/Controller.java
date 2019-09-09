package com.bassis.boot.web.annotation;

import com.bassis.bean.annotation.Component;
import com.bassis.bean.annotation.Scope;
import com.bassis.bean.common.enums.ScopeEnum;

import java.lang.annotation.*;

/**
 * 自定义action代理注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Component
@Scope(value = ScopeEnum.PROTOTYPE)
public @interface Controller {

    String value() default "";
}
