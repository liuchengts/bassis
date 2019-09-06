package com.bassis.boot.web.annotation;

import java.lang.annotation.*;

/**
 * 寻址
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RequestMapping {
	 String value()  default "";
}
