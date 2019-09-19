package com.bassis.data.annotation;

import com.bassis.bean.annotation.Component;
import com.bassis.bean.common.enums.ScopeEnum;

import java.lang.annotation.*;

/**
 * db注入资源注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component(scope = ScopeEnum.PROTOTYPE)
public @interface Data {
	String value()  default "";
}
