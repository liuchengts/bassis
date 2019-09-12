package com.bassis.bean.annotation.impl;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.Scanner;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.common.enums.ScopeEnum;
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
     * class Controller 存储器， name:class
     */
    private static final Map<String, Class<?>> beansControllerObject = new ConcurrentHashMap<>();
    /**
     * bean Controller 别名存储器， 原始名称:别名
     */
    private static final Map<String, String> aliasControllerBeanName = new ConcurrentHashMap<>();
    /**
     * bean Controller 控制器范围存储器， class:范围
     */
    private static final Map<Class<?>, ScopeEnum> scopecControllerBean = new ConcurrentHashMap<>();
    /**
     * bean Controller 中的方法存储器， beanName:Methods:@aop true
     */
    private static final Map<String, Map<Method, Boolean>> beanControllerMethods = new ConcurrentHashMap<>();
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
        //初始化class
        initializeClass();
    }

    /**
     * 根据名称获取 class的实例名
     *
     * @param name 名称
     * @return 返回class的实例名
     */
    private static String getClassMatchByName(String name) {
        String className = "";
        if (beansObject.containsKey(name)) {
            className = beansObject.get(name).getName();
        } else if (aliasBeanName.containsKey(name)) {
            className = aliasBeanName.get(name);
        } else if (beansControllerObject.containsKey(name)) {
            className = beansControllerObject.get(name).getName();
        } else if (aliasControllerBeanName.containsKey(name)) {
            className = aliasControllerBeanName.get(name);
        }
        return className;
    }

    /**
     * 根据bean class获得Class实例范围
     *
     * @param clz class实例
     * @return 返回class范围
     */
    public static ScopeEnum getScopecControllerBean(Class<?> clz) {
        if (scopecControllerBean.containsKey(clz)) return scopecControllerBean.get(clz);
        return null;
    }

    /**
     * 检查class是否带有范围注解 默认为单实例
     *
     * @param aclass 要检测的class
     * @return 单实例为true 多实例为false
     */
    public static boolean isScopeSingleton(Class<?> aclass) {
        boolean fag;
        if (!aclass.isAssignableFrom(Component.class)) {
            ScopeEnum scopeEnum = ComponentImpl.getScopecControllerBean(aclass);
            fag = null == scopeEnum || scopeEnum != ScopeEnum.PROTOTYPE;
        } else {
            fag = aclass.getAnnotation(Component.class).scope().equals(ScopeEnum.SINGLETON);
        }
        return fag;
    }

    /**
     * 根据beanClass获得Class
     *
     * @param clz bean的class
     * @return 返回class
     */
    public static Class<?> getClassByClass(Class<?> clz) {
        return getClassByName(clz.getName());
    }

    /**
     * 根据bean name获得Class
     *
     * @param name bean的 name
     * @return 返回class
     */
    public static Class<?> getClassByName(String name) {
        name = getClassMatchByName(name);
        if (beansObject.containsKey(name)) return beansObject.get(name);
        else return beansControllerObject.getOrDefault(name, null);
    }

    /**
     * 根据bean的class获得当前所有的的方法
     *
     * @param clz bean的class
     * @return 当前的所有方法:aop true
     */
    public static Map<Method, Boolean> getClassAllMethods(Class<?> clz) {
        return getClassAllMethods(clz.getName());
    }

    /**
     * 根据bean的class获得当前带有@Aop的方法
     *
     * @param clz bean的class
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getClassAopMethods(Class<?> clz) {
        return getClassAopMethods(clz.getName());
    }

    /**
     * 根据bean的class获得当前不带@Aop的方法
     *
     * @param clz bean的class
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getClassNotAopMethods(Class<?> clz) {
        return getClassMethods(clz.getName());
    }

    /**
     * 根据bean的name获得当前所有的的方法
     *
     * @param name bean的name
     * @return 当前的所有方法:aop true
     */
    public static Map<Method, Boolean> getClassAllMethods(String name) {
        String key = getClassMatchByName(name);
        if (beanMethods.containsKey(key)) return beanMethods.get(key);
        else return beanControllerMethods.getOrDefault(key, null);
    }

    /**
     * 根据bean的name获得当前带有@Aop的方法
     *
     * @param name bean的name
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getClassAopMethods(String name) {
        String key = getClassMatchByName(name);
        if (beanMethods.containsKey(key))
            return beanMethods.get(key).entrySet().stream().filter(map -> Boolean.TRUE.equals(map.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
        else if (beanControllerMethods.containsKey(key))
            return beanControllerMethods.get(key).entrySet().stream().filter(map -> Boolean.TRUE.equals(map.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
        else return null;
    }

    /**
     * 根据bean的name获得当前不带@Aop的方法
     *
     * @param name bean的name
     * @return 当前的所有带aop的方法
     */
    public static Set<Method> getClassMethods(String name) {
        String key = getClassMatchByName(name);
        if (beanMethods.containsKey(key))
            return beanMethods.get(key).entrySet().stream().filter(map -> Boolean.FALSE.equals(map.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
        else if (beanControllerMethods.containsKey(key))
            return beanControllerMethods.get(key).entrySet().stream().filter(map -> Boolean.FALSE.equals(map.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
        else return null;
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
     * 增加一个没有Component注解的class为 Component
     *
     * @param clz       class实例
     * @param scopeEnum class实例范围
     */
    public static void addComponent(Class<?> clz, ScopeEnum scopeEnum) {
        logger.debug(clz.getName());
        //别名
        String aliasName = clz.getSimpleName();
        //原始名称
        String name = clz.getName();
        if (beansControllerObject.containsKey(name))
            CustomException.throwOut(" Controller @Component bean [" + name + "] repeat:" + name);
        if (!StringUtils.isEmptyString(aliasName)) {
            if (aliasControllerBeanName.containsKey(aliasName))
                CustomException.throwOut(" Controller @Component bean [" + name + "] aliasName repeat:" + aliasName);
            aliasControllerBeanName.put(aliasName, clz.getName());
        }
        Map<Method, Boolean> mapMethod = new HashMap<>();
        beansControllerObject.put(name, clz);
        scopecControllerBean.put(clz, scopeEnum);
        Arrays.stream(clz.getDeclaredMethods()).forEach(method -> mapMethod.put(method, AopImpl.isAop(method)));
        beanControllerMethods.put(name, mapMethod);
    }

    /**
     * 初始化bean
     */
    private static void initializeClass() {
        //开始由 BeanFactory 根据 class创建 bean对象
        beansObject.values().forEach(clz -> beanFactory.newBeanTask(clz));
    }

}
