package com.bassis.bean.annotation;

import java.lang.annotation.*;

/**
 * 自定义aop注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Aop {

    /**
     * 要执行的aop的对象的别名，对应@Component的name
     * 与 aclass()二选一
     * 最高优先级
     */
    String value() default "";

    /**
     * 要执行的aop的对象的class，对应@Component的class实例
     * 与 value()二选一
     */
    Class<?> aclass() default Aop.class;

    /**
     * 要执行aop时除了方法本身参数外，还需要的额外参数
     * 参数组成顺序是，当前parameters数组参数后面加上当前带有@Aop方法的所有入参，按照顺序排列,参数为null是用字符串"null"表示
     * 举例  parameters = {"a", "b", "c"} 即实际的aop方法最前面三个参数为 a  b  c  之后才是@Aop方法本身的入参
     */
    String[] parameters() default "";
}
