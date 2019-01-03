package org.bassis.bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义注入资源注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
	String value()  default "";
	String verify() default "";
	Class<?> clas() default Object.class;
}
