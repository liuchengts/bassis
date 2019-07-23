package com.bassis.web.annotation.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bassis.bean.Scanner;
import com.bassis.bean.annotation.impl.AutowiredImpl;
import org.apache.log4j.Logger;

import com.bassis.tools.string.StringUtils;
import com.bassis.web.annotation.Controller;
import com.bassis.web.annotation.RequestMapping;

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

    // 包请求注解路径/包路径
    private static Map<String, Class<?>> mapClass;
    // 包请求注解路径 、 方法注解路径/方法名称
    private static Map<String, Map<String, Method>> mapMethod;

    // 只处理当前实现类的注解
    static {
        logger.debug("@Controller分析开始");
        mapClass = new HashMap<>();
        mapMethod = new HashMap<>();
        for (Class<?> clz : scanPackageList) {
            if (clz.isAnnotationPresent(Controller.class)) {
                analyse(clz);
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
        if (!mapClass.containsKey(key)) return null;
        return mapClass.get(key);
    }

    /**
     * 根据请求路径获取方法
     *
     * @param key 包请求注解路径
     * @return 方法注解路径/方法名称
     */
    public static Map<String, Method> getMapMethod(String key) {
        if (!mapMethod.containsKey(key)) return null;
        return mapMethod.get(key);
    }

    /**
     * 处理 Controller注解 与 RequestMapping注解
     *
     * @param clz
     */
    private static void analyse(Class<?> clz) {
        logger.debug(clz.getName());
        Controller annotation = clz.getAnnotation(Controller.class);
        // 输出注解上的属性
        String path = annotation.value();
        if (StringUtils.isEmptyString(path)) {
            // 查找有没有RequestMapping注解
            path = clz.isAnnotationPresent(RequestMapping.class) ? clz.getAnnotation(RequestMapping.class).value()
                    : path;
            // 如果没有给定注解值 那么让它以当前包的上级包路径+控制器名称 为请求路径 如 org.modao.controllers.Test
            // 请求路径为 /controllers/Test
            path = StringUtils.isEmptyString(path) ? ("/"
                    + StringUtils.subStringCustom(clz.getName(), clz.getSimpleName(), ".") + "/" + clz.getSimpleName())
                    : path;
        }
        // 如果两个控制器注解路径相同 策略是注解路径+类路径
        if (mapClass.containsKey(path)) {
            Class<?> _clz = getMapClass(path);
            String _path = path + "/" + _clz.getSimpleName();
            mapClass.put(_path, _clz);
            mapClass.remove(path);
            mapMethod.remove(path);
            analyseMethods(_path, _clz);
            logger.debug("找到了重复path :" + path + "|更改为 _path：" + _path);
            _path = path + "/" + clz.getSimpleName();
            mapClass.put(_path, clz);
            analyseMethods(_path, clz);
            logger.debug("原本重复path :" + path + "|更改为 _path：" + _path);
        } else {
            logger.debug("path:" + path);
            mapClass.put(path, clz);
            analyseMethods(path, clz);
        }
    }

    /**
     * 方法注解分析
     *
     * @param path
     * @param clz
     */
    private static void analyseMethods(String path, Class<?> clz) {
        // 分析方法
        Method[] methods = clz.getDeclaredMethods();
        Map<String, Method> map = getMapMethod(path);
        map = null == map ? new HashMap<>() : map;
        for (Method method : methods) {
            if (!method.isAnnotationPresent(RequestMapping.class)) continue;
            // 得到注解
            RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
            // 输出注解属性
            String method_path = methodAnnotation.value();
            // 如果没有给定注解值 那么让它以当方法名称 为请求路径
            method_path = StringUtils.isEmptyString(method_path) ? ("/" + method.getName()) : method_path;
            // 如果两个方法注解路径相同 策略是注解路径+方法名
            if (map.containsKey(method_path)) {
                Method _method = map.get(method_path);
                String _path = method_path + "/" + _method.getName();
                map.put(_path, _method);
                map.remove(method_path);
                logger.debug("找到了重复method_path :" + method_path + "|更改为 _path：" + _path);
                _path = method_path + "/" + method.getName();
                map.put(_path, method);
                logger.debug("原本重复method_path :" + method_path + "|更改为 _path：" + _path);
            } else {
                logger.debug("path:" + path);
                map.put(method_path, method);
            }
        }
        mapMethod.put(path, map);
    }

    /**
     * 动态调用 autowired 进行注入，这个过程应该在寻址完成之后进行
     */
    public static void autowired(Object object) {
        AutowiredImpl.analyseFields(object);
    }
}
