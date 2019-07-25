package com.bassis.bean.annotation.impl;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.common.Bean;
import com.bassis.bean.common.FieldBean;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.AutowiredEvent;
import com.bassis.bean.proxy.ProxyFactory;
import org.apache.log4j.Logger;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.ReflexUtils;
import com.bassis.tools.string.StringUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 对当前所有资源的自动注入进行实现
 *
 * @see Autowired
 */
public class AutowiredImpl implements ApplicationListener<AutowiredEvent> {
    private static Logger logger = Logger.getLogger(AutowiredImpl.class);
    private static BeanFactory beanFactory = BeanFactory.getInstance();
    private static Set<FieldBean> fieldBeans = new HashSet<>();

    /**
     * 全局字段注解分析
     *
     * @param object     当前类
     * @param superClass 是否从父类获取字段
     */
    public void analyseFields(Object object, boolean superClass) {
        Field[] fields;
        if (superClass) {
            fields = object.getClass().getSuperclass().getDeclaredFields();
        } else {
            fields = object.getClass().getDeclaredFields();
        }
        for (Field field : fields) {
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
    private void fieldAutowired(Object obj, Field field) {
        String position = "[fieldAutowired] bean:" + obj.getClass().getName() + " field:" + field.getName();
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
            }  //基本数据类型

            if (null != fieldClass) {
                beanFactory.newBeanTask(fieldClass);
            }
        } catch (Exception e) {
            logger.error(position + " 字段参数注入失败", e);
        }
    }

    /**
     * 执行注入
     */
    private void twoStageAutowired() {
        fieldBeans.forEach(this::fieldBeanAutowired);
    }

    /**
     * 单个bean注入
     *
     * @param fieldBean 要操作的fieldBean
     */
    private void fieldBeanAutowired(FieldBean fieldBean) {
        String position = "[twoStageAutowired] bean: " + fieldBean.getObject().getClass().getName() + "field:" + fieldBean.getField().getName();
        Bean bean = beanFactory.getBean(fieldBean.getFieldClass());
        if (null == bean) {
            CustomException.throwOut(position + " @Autowired not resource bean");
        }
        assert bean != null;
        Object fieldObject = bean.getObject();
        if (null == fieldObject) {
            CustomException.throwOut(position + " @Autowired not resource object");
        }
        try {
            fieldBean.getField().set(fieldBean.getObject(), fieldObject);
        } catch (IllegalAccessException e) {
            logger.error(position + " 字段参数注入失败", e);
        }
        logger.debug(position + " 字段参数注入成功");
    }

    @Override
    public void onApplicationEvent(AutowiredEvent var1) {
        Class<?> aclass = (Class<?>) var1.getSource();
        assert aclass != null;
        if (!fieldBeans.contains(aclass)) return;
        Optional<FieldBean> optionalFieldBean = fieldBeans.stream().filter(fBean -> fBean.getFieldClass().equals(aclass)).findFirst();
        if (!optionalFieldBean.isPresent()) {
            logger.debug(aclass.getName() + " 参数注入失败");
            return;
        }
        fieldBeanAutowired(optionalFieldBean.get());
    }
}
