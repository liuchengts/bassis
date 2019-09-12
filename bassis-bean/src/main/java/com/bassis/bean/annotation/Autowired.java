package com.bassis.bean.annotation;

import java.lang.annotation.*;

/**
 * 自定义注入资源注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface Autowired {
    /**
     * 要注入的对象的别名，对应@Component的name
     * 第一优先级
     */
    String value() default "";

    /**
     * 要注入的对象资源校验，目前暂不启用此功能
     */
    String verify() default "";

    /**
     * 要注入的对象的别名，对应一个实际的class实例
     */
    Class<?> aclass() default Autowired.class;
}
