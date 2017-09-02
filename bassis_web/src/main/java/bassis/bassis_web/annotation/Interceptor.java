package bassis.bassis_web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自定义拦截器代理注解
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
	String value() ;
}
