package com.bassis.bean.annotation.impl;

import com.bassis.bean.Scanner;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.proxy.ProxyFactory;
import org.apache.log4j.Logger;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.string.StringUtils;

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
    private static Set<Class<?>> scanPackageList = Scanner.getInstance().getPackageList();

    /**
     * bean存储器， name:class
     */
    private static Map<String, Object> beansObject;
    /**
     * bean别名存储器， 原始名称:别名
     */
    private static Map<String, String> aliasBeanName;
    /**
     * bean中的方法存储器， beanName:Methods
     */
    private static Map<String, Set<Method>> beanMethods;

    /**
     * 只处理当前实现类的注解
     */
    static {
        logger.debug("@Component分析开始");
        beansObject = new HashMap<>();
        aliasBeanName = new HashMap<>();
        beanMethods = new HashMap<>();
        for (Class<?> clz : scanPackageList) {
            try {
                if (clz.isAnnotationPresent(Component.class)) analyse(clz);
            } catch (Exception e) {
                CustomException.throwOut("@Component分析异常：", e);
            }
        }
        autowired();
    }


    /**
     * 根据bean名称获得bean
     *
     * @param name bean别名
     * @return 返回class
     */
    public static Object getBeansClass(String name) {
        String key = "";
        if (!beansObject.containsKey(name)) {
            if (aliasBeanName.containsKey(name)) {
                key = aliasBeanName.get(name);
            }
        } else {
            key = name;
        }
        return beansObject.get(key);
    }

    /**
     * 根据bean名称获得bean
     *
     * @param clz bean的class
     * @return 返回class
     */
    public static Object getBeansClass(Class clz) {
        String key = clz.getName();
        if (!beansObject.containsKey(key)) {
            return null;
        }
        return beansObject.get(key);
    }

    /**
     * 根据bean的class获得当前的方法
     *
     * @param clz bean的class
     * @return 当前的所有方法
     */
    public static Set<Method> getBeanMethods(Class clz) {
        return getBeanMethods(clz.getName());
    }

    /**
     * 根据bean的class获得当前的方法
     *
     * @param name bean的name
     * @return 当前的所有方法
     */
    public static Set<Method> getBeanMethods(String name) {
        String key = "";
        if (!beansObject.containsKey(name)) {
            if (aliasBeanName.containsKey(name)) {
                key = aliasBeanName.get(name);
            }
        } else {
            key = name;
        }
        if (!beanMethods.containsKey(key)) {
            return null;
        }
        return beanMethods.get(key);
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
        if (beansObject.containsKey(name)) {
            CustomException.throwOut(" @Component bean [" + name + "] repeat:" + name);
        }
        if (!StringUtils.isEmptyString(aliasName)) {
            if (aliasBeanName.containsKey(aliasName)) {
                CustomException.throwOut(" @Component bean [" + name + "] aliasName repeat:" + aliasName);
            }
            aliasBeanName.put(aliasName, clz.getName());
        }
        Object object = ProxyFactory.invoke(clz);
        Set<Method> setMethod = new HashSet<>();
        Collections.addAll(setMethod, clz.getDeclaredMethods());
        beansObject.put(name, object);
        beanMethods.put(name, setMethod);
    }

    /**
     * 处理当前 beansObject 集的 autowired 注解
     */
    private static void autowired() {
        beansObject.entrySet().forEach(m -> {
            Object obj = m.getValue();
            AutowiredImpl.analyseFields(obj);
            //更新存储对象
            beansObject.put(m.getKey(), obj);
        });
    }

}
