package com.bassis.hibernate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * db注入资源注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {
	String value()  default "";
}
