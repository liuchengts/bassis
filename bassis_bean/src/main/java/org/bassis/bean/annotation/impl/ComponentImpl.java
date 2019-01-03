package org.bassis.bean.annotation.impl;

import org.apache.log4j.Logger;
import org.bassis.bean.Scanner;
import org.bassis.bean.annotation.Component;
import org.bassis.tools.exception.CustomException;
import org.bassis.tools.string.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 处理bean类型注解
 *
 * @see Component
 */
public class ComponentImpl {
    private static Logger logger = Logger.getLogger(ComponentImpl.class);

    private static class LazyHolder {
        private static final ComponentImpl INSTANCE = new ComponentImpl();
    }

    private ComponentImpl() {
    }

    public static final ComponentImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 扫描到的class
     */
    static Set<Class<?>> scanPackageList = Scanner.getInstance().getPackageList();

    /**
     * bean存储器， name:class
     */
    static Map<String, Class> beansClass;
    /**
     * bean别名存储器， 原始名称:别名
     */
    static Map<String, String> aliasBeanName;
    /**
     * bean中的方法存储器， beanName:Methods
     */
    static Map<String, Set<Method>> beanMethods;

    /**
     * 根据bean名称获得bean
     *
     * @param name bean别名
     * @return 返回class
     */
    public static Class getBeansClass(String name) {
        String key = "";
        if (!beansClass.containsKey(name)) {
            if (aliasBeanName.containsKey(name)) {
                key = aliasBeanName.get(name);
            }
        } else {
            key = name;
        }
        return beansClass.get(key);
    }

    /**
     * 根据bean名称获得bean
     *
     * @param clz bean的class
     * @return 返回class
     */
    public static Class getBeansClass(Class clz) {
        String key = clz.getName();
        if (!beansClass.containsKey(key)) {
            return null;
        }
        return beansClass.get(key);
    }

    /**
     * 根据bean的class获得当前的方法
     *
     * @param clz bean的class
     * @return 当前的所有方法
     */
    public static Set<Method> getBeanMethods(Class clz) {
        String key = clz.getName();
        if (!beanMethods.containsKey(key)) {
            return null;
        }
        return beanMethods.get(key);
    }

    /**
     * 只处理当前实现类的注解
     */
    static {
        logger.debug("@Component分析开始");
        beansClass = new HashMap<>();
        aliasBeanName = new HashMap<>();
        beanMethods = new HashMap<>();
        for (Class<?> clz : scanPackageList) {
            try {
                if (clz.isAnnotationPresent(Component.class)) analyse(clz);
            } catch (Exception e) {
                CustomException.throwOut("@Component分析异常：", e);
            }
        }
    }

    /**
     * 开始分析注解
     *
     * @param clz class实例
     */
    private static void analyse(Class<?> clz) {
        logger.debug(clz.getName());
        Component annotation = clz.getAnnotation(Component.class);
        //别名
        String aliasName = annotation.name();
        //原始名称
        String name = clz.getName();
        if (beansClass.containsKey(name)) {
            CustomException.throwOut(" @Component bean [" + name + "] repeat:" + name);
        }
        if (!StringUtils.isEmptyString(aliasName)) {
            if (aliasBeanName.containsKey(aliasName)) {
                CustomException.throwOut(" @Component bean [" + name + "] aliasName repeat:" + aliasName);
            }
            aliasBeanName.put(aliasName, clz.getName());
        }
        Set<Method> setMethod = new HashSet<>();
        Collections.addAll(setMethod, clz.getDeclaredMethods());
        beansClass.put(name, clz);
        beanMethods.put(name, setMethod);

    }
}
