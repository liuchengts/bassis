package org.bassis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义bean注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
	String name()  default "";
}
