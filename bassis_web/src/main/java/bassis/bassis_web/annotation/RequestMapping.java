package bassis.bassis_web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 寻址
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
	 String value()  default "";
}
