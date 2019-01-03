package org.bassis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义aop注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Aop {
	String value();
}
