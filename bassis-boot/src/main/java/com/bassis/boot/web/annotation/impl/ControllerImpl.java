package com.bassis.boot.web.annotation.impl;

import com.bassis.bean.Scanner;
import com.bassis.boot.web.annotation.Controller;
import com.bassis.boot.web.annotation.RequestMapping;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.string.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理 Controller.class RequestMapping.class注解
 */
public class ControllerImpl {
    private static Logger logger = Logger.getLogger(ControllerImpl.class);

    private static class LazyHolder {
        private static final ControllerImpl INSTANCE = new ControllerImpl();
    }

    public static final ControllerImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 扫描到的class
     */
    private static Set<Class<?>> scanPackageList = Scanner.getInstance().getPackageList();

    //请求路径/方法
    private static Map<String, Method> methodMap = new ConcurrentHashMap<>();
    //请求路径/类
    private static Map<String, Class> clazMap = new ConcurrentHashMap<>();

    // 只处理当前实现类的注解
    static {
        logger.debug("@Controller分析开始");
        for (Class<?> clz : scanPackageList) {
            try {
                if (clz.isAnnotationPresent(Controller.class)) analyse(clz);
            } catch (Exception e) {
                CustomException.throwOut("@Controller分析异常：", e);
            }
        }
    }

    /**
     * 根据请求路径获取控制器
     *
     * @param key 包请求注解路径
     * @return 返回包路径
     */
    public static Class<?> getMapClass(String key) {
        if (!clazMap.containsKey(key)) return null;
        return clazMap.get(key);
    }

    /**
     * 根据请求路径获取方法
     *
     * @param key 包请求注解路径
     * @return 方法注解路径/方法名称
     */
    public static Method getMapMethod(String key) {
        if (!methodMap.containsKey(key)) return null;
        return methodMap.get(key);
    }

    /**
     * 处理 Controller注解 与 RequestMapping注解
     *
     * @param clz 带有@Controller的类
     */
    private static void analyse(Class<?> clz) {
        logger.debug(clz.getName());
        Controller annotation = clz.getAnnotation(Controller.class);
        String path = annotation.value();
        // 如果没有给定注解值 那么让它以当前包的上级包路径+控制器名称 为请求路径 如 org.modao.controllers.Test
        // 请求路径为 /controllers/Test
        path = StringUtils.isEmptyString(path) ? ("/"
                + StringUtils.subStringCustom(clz.getName(), clz.getSimpleName(), ".") + "/" + clz.getSimpleName())
                : path;
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            // 输出注解属性
            String method_path = "";
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                method_path = methodAnnotation.value();
            }
            // 如果没有给定注解值 那么让它以当方法名称 为请求路径
            method_path = StringUtils.isEmptyString(method_path) ? ("/" + method.getName()) : method_path;
            method_path = path + method_path;
            //检测是否重复url
            if (clazMap.containsKey(method_path)) CustomException.throwOut("控制器类异常，重复的url：" + method_path);
            if (methodMap.containsKey(method_path)) CustomException.throwOut("控制器方法异常，重复的url：" + method_path);
            clazMap.put(method_path, clz);
            methodMap.put(method_path, method);
        }
    }
}
