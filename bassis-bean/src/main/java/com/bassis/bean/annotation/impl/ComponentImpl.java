package com.bassis.bean.annotation.impl;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.Scanner;
import com.bassis.bean.annotation.Component;
import org.apache.log4j.Logger;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.string.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 处理bean类型注解
 *
 * @see Component
 */
public class ComponentImpl {
    private static Logger logger = Logger.getLogger(ComponentImpl.class);
    private static BeanFactory beanFactory = BeanFactory.getInstance();

    private static class LazyHolder {
        private static final ComponentImpl INSTANCE = new ComponentImpl();
    }

    private ComponentImpl() {
    }

    public static ComponentImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * class存储器， name:class
     */
    private static final Map<String, Class<?>> beansObject = new ConcurrentHashMap<>();
    /**
     * bean别名存储器， 原始名称:别名
     */
    private static final Map<String, String> aliasBeanName = new ConcurrentHashMap<>();
    /**
     * bean中的方法存储器， beanName:Methods:@aop true
     */
    private static final Map<String, Map<Method, Boolean>> beanMethods = new ConcurrentHashMap<>();

    static {
        logger.debug("@Component分析开始");
        for (Class<?> clz : Scanner.getInstance().getPackageList()) {
            try {
                if (clz.isAnnotationPresent(Component.class)) analyse(clz);
            } catch (Exception e) {
                CustomException.throwOut("@Component分析异常：", e);
            }
        }
        //初始化bean
        initializeBean();
    }

    /**
     * 根据bean名称获得Class
     *
     * @param name bean别名
     * @return 返回class
     */
    public static Class<?> getBeansClass(String name) {
        String key = "";
        if (!beansObject.containsKey(name)) {
            if (aliasBeanName.containsKey(name)) key = aliasBeanName.get(name);
        } else {
            key = name;
        }
        return beansObject.get(key);
    }

    /**
     * 根据beanClass获得Class
     *
     * @param clz bean的class
     * @return 返回class
     */
    public static Class<?> getBeansClass(Class<?> clz) {
        String key = clz.getName();
        if (!beansObject.containsKey(key)) {
            return null;
        }
        return beansObject.get(key);
    }

    /**
     * 根据bean的class获得当前所有的的方法
     *
     * @param clz bean的class
     * @return 当前的所有方法:aop true
     */
    public static Map<Method, Boolean> getBeanAllMethods(Class<?> clz) {
        return getBeanAllMethods(clz.getName());
    }

    /**
     * 根据bean的class获得当前带有@Aop的方法
     *
     * @param clz bean的class
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getBeanAopMethods(Class<?> clz) {
        return getBeanAopMethods(clz.getName());
    }

    /**
     * 根据bean的class获得当前不带@Aop的方法
     *
     * @param clz bean的class
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getBeanMethods(Class<?> clz) {
        return getBeanMethods(clz.getName());
    }

    /**
     * 根据bean的name获得当前所有的的方法
     *
     * @param name bean的name
     * @return 当前的所有方法:aop true
     */
    public static Map<Method, Boolean> getBeanAllMethods(String name) {
        String key = "";
        if (!beansObject.containsKey(name)) {
            if (aliasBeanName.containsKey(name)) key = aliasBeanName.get(name);
        } else {
            key = name;
        }
        if (!beanMethods.containsKey(key)) return null;
        return beanMethods.get(key);
    }

    /**
     * 根据bean的name获得当前带有@Aop的方法
     *
     * @param name bean的name
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getBeanAopMethods(String name) {
        String key = "";
        if (!beansObject.containsKey(name)) {
            if (aliasBeanName.containsKey(name)) key = aliasBeanName.get(name);
        } else {
            key = name;
        }

        if (!beanMethods.containsKey(key)) return null;
        return beanMethods.get(key).entrySet().stream().filter(map -> Boolean.TRUE.equals(map.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * 根据bean的name获得当前不带@Aop的方法
     *
     * @param name bean的name
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getBeanMethods(String name) {
        String key = "";
        if (!beansObject.containsKey(name)) {
            if (aliasBeanName.containsKey(name)) key = aliasBeanName.get(name);
        } else {
            key = name;
        }
        if (!beanMethods.containsKey(key)) return null;
        return beanMethods.get(key).entrySet().stream().filter(map -> Boolean.FALSE.equals(map.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * 处理 Component 注解
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
        if (beansObject.containsKey(name)) CustomException.throwOut(" @Component bean [" + name + "] repeat:" + name);
        if (!StringUtils.isEmptyString(aliasName)) {
            if (aliasBeanName.containsKey(aliasName))
                CustomException.throwOut(" @Component bean [" + name + "] aliasName repeat:" + aliasName);
            aliasBeanName.put(aliasName, clz.getName());
        }
        Map<Method, Boolean> mapMethod = new HashMap<>();
        beansObject.put(name, clz);
        Arrays.stream(clz.getDeclaredMethods()).forEach(method -> mapMethod.put(method, AopImpl.isAop(method)));
        beanMethods.put(name, mapMethod);
    }

    /**
     * 初始化bean
     */
    private static void initializeBean() {
        //开始由 BeanFactory 根据 class创建 bean对象
        beansObject.values().forEach(clz -> beanFactory.newBeanTask(clz));
    }

}
