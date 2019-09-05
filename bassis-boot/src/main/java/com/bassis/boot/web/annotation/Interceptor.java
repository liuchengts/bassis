package com.bassis.boot.web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义拦截器代理注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
	/**
	 * 要执行的 Interceptor 的对象的别名，对应@Component的name
	 * 与 aclass()二选一
	 */
	String value() default "";

	/**
	 * 要执行的 Interceptor 的对象的class，对应@Component的class实例
	 * 与 value()二选一
	 */
	Class<?> aclass() default Object.class;

}
