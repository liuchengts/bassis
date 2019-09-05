package com.bassis.boot.web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义action代理注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
	 String value()  default "";
}
