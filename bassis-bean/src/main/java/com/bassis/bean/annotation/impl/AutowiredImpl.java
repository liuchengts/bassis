package com.bassis.bean.annotation.impl;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.proxy.ProxyFactory;
import org.apache.log4j.Logger;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.ReflexUtils;
import com.bassis.tools.string.StringUtils;

import java.lang.reflect.Field;

/**
 * 对当前所有资源的自动注入进行实现
 *
 * @see Autowired
 */
public class AutowiredImpl {
    private static Logger logger = Logger.getLogger(AutowiredImpl.class);

    /**
     * 全局字段注解分析
     *
     * @param object 当前类
     * @param superClass 是否从父类获取字段
     */
    public static void analyseFields(Object object, boolean superClass) {
        Field[] fields;
        if (superClass) {
            fields = object.getClass().getSuperclass().getDeclaredFields();
        } else {
            fields = object.getClass().getDeclaredFields();
        }
        for (Field field : fields) {
            logger.info(field.getName());
            if (field.isAnnotationPresent(Autowired.class)) {
                fieldAutowired(object, field);
            }
        }
    }

    /**
     * 字段属性注入
     * 这个方法不支持dao注入
     *
     * @param obj   当前类
     * @param field 要注入的字段
     */
    private static void fieldAutowired(Object obj, Field field) {
        String position = "bean:" + obj.getClass().getName() + " field:" + field.getName();
        try {
            field.setAccessible(true);
            Class<?> cla = field.getType();
            Autowired annotation = field.getAnnotation(Autowired.class);
            // 输出注解上的属性
            String value = annotation.value();
            Class<?> aclass = annotation.aclass();
            Class fieldClass = null;
            if (!ReflexUtils.isWrapClass(field.getType().getName())) {
                //不是基础类型
                if (null != aclass) {
                    fieldClass = ComponentImpl.getBeansClass(aclass);
                } else if (!StringUtils.isEmptyString(value)) {
                    fieldClass = ComponentImpl.getBeansClass(value);
                } else {
                    if (cla.isInterface()) {
                        CustomException.throwOut(position + " @Autowired not resource");
                    } else {
                        //如果是其他类型 没有参数声明 直接new当前类型
                        fieldClass = cla;
                    }
                }
            } else if (!ReflexUtils.isWrapClass_Pack(field.getType().getName())) {
                //是基础类型的包装类型
                fieldClass = field.getType().getClass();
            } else {
                //基本数据类型
            }
            Object fieldObject = null;
            if (null != fieldClass) {
                fieldObject = ProxyFactory.invoke(fieldClass);
            }
            if (null == fieldObject) {
                CustomException.throwOut(position + " @Autowired not resource object");
            }
            field.set(obj, fieldObject);
            logger.debug(position + " 字段参数注入成功");
        } catch (Exception e) {
            logger.error(position + " 字段参数注入失败", e);
        }
    }
}
