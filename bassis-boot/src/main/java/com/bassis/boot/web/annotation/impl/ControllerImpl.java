package com.bassis.boot.web.annotation.impl;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.Scanner;
import com.bassis.bean.annotation.Component;
import com.bassis.boot.web.annotation.Controller;
import com.bassis.boot.web.annotation.RequestMapping;
import com.bassis.boot.web.annotation.RequestParam;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.string.StringUtils;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理 Controller.class RequestMapping.class注解
 */
public class ControllerImpl {
    private static Logger logger = LoggerFactory.getLogger(ControllerImpl.class);
    private static class LazyHolder {
        private static final ControllerImpl INSTANCE = new ControllerImpl();
    }

    public static ControllerImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    final static BeanFactory beanFactory = BeanFactory.getInstance();
    //请求路径/类
    private static Map<String, Class> clazMap = new ConcurrentHashMap<>();
    //请求路径/方法
    private static Map<String, Method> methodMap = new ConcurrentHashMap<>();
    //方法/参数名，是否必须
    private static Map<Method, List<Object>> parameterMap = new ConcurrentHashMap<>();

    // 只处理当前实现类的注解
    static {
        logger.debug("@Controller分析开始");
        for (Class<?> clz : Scanner.getInstance().getPackageList()) {
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
     * 根据请求路径获取方法
     *
     * @param key 方法
     * @return 方法参数名称  方法类型  是否必须
     */
    public static List<Object> getMapParameter(Method key) {
        if (!parameterMap.containsKey(key)) return new ArrayList<>();
        return parameterMap.get(key);
    }

    /**
     * 处理 Controller注解 与 RequestMapping注解
     *
     * @param clz 带有@Controller的类
     */
    private static void analyse(Class<?> clz) {
        logger.debug(clz.getName());
        Controller annotation = clz.getAnnotation(Controller.class);
        //处理 Component注解
        Component component = Controller.class.getAnnotation(Component.class);
        //加入beanfactory
        beanFactory.addComponent(clz, component.scope());
        String path = annotation.value();
        // 如果没有给定注解值 那么让它以当前包的上级包路径+控制器名称 为请求路径 如 org.modao.controllers.Test
        // 请求路径为 /controllers/Test
        path = StringUtils.isEmptyString(path) ? ("/"
                + StringUtils.subStringCustom(clz.getName(), clz.getSimpleName(), ".") + "/" + clz.getSimpleName())
                : path;

        Method[] methods = clz.getDeclaredMethods();
        Set<Method> methodSet = new LinkedHashSet<>();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(RequestMapping.class)) continue;
            methodSet.add(method);
            // 输出注解属性
            RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
            String method_path = methodAnnotation.value();
            method_path = StringUtils.isEmptyString(method_path) ? ("/" + method.getName()) : method_path;
            method_path = path + method_path;
            //检测是否重复url
            if (clazMap.containsKey(method_path)) CustomException.throwOut("控制器类异常，重复的url：" + method_path);
            if (methodMap.containsKey(method_path)) CustomException.throwOut("控制器方法异常，重复的url：" + method_path);
            //进行路径注册
            clazMap.put(method_path, clz);
            methodMap.put(method_path, method);
        }
        Map<Method, List<String>> methodListMap = getMethodParameterName(clz, methodSet);
        //分析方法内的参数注解
        methodListMap.forEach(ControllerImpl::getMethodParameterByAnnotation);
    }


    /**
     * 分析方法参数注解
     *
     * @param method 要获取参数名的方法
     */
    private static void getMethodParameterByAnnotation(Method method, List<String> lists) {
        List<Object> objectList = new ArrayList<>();
        int index = -1;
        for (Parameter parameter : method.getParameters()) {
            index++;
            if (!parameter.isAnnotationPresent(RequestParam.class)) continue;
            RequestParam annotation = parameter.getAnnotation(RequestParam.class);
            String name = annotation.name();
            if (StringUtils.isEmptyString(name)) name = lists.get(index);
            boolean required = annotation.required();
            objectList.add(name);
            objectList.add(parameter.getType());
            objectList.add(required);
        }
        parameterMap.put(method, objectList);
    }

    private static Map<Method, List<String>> getMethodParameterName(Class<?> clazz, Set<Method> methodSet) {
        Map<String, List<Object>> methodTypeMap = new HashMap<>();
        Map<Method, List<String>> methods = new HashMap<>();
        ClassReader classReader = null;
        try {
            classReader = new ClassReader(clazz.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        methodSet.forEach(method -> {
            List<Object> list = new ArrayList<>();
            list.add(method);
            Class<?>[] parameterTypes = method.getParameterTypes();
            Type[] types = new Type[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                types[i] = Type.getType(parameterTypes[i]);
            }
            list.add(types);
            methodTypeMap.put(method.getName(), list);
        });
        assert classReader != null;
        classReader.accept(new ClassVisitor(Opcodes.ASM4) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                //方法级别
                if (!methodTypeMap.containsKey(name)) return null;
                //获取方法
                Type[] argumentTypes = Type.getArgumentTypes(desc);
                List<Object> objectList = methodTypeMap.get(name);
                Method method = (Method) objectList.get(0);
                Type[] types = (Type[]) objectList.get(1);
                if (!Arrays.equals(argumentTypes, types)) return null;
                List<String> parameterList = new ArrayList<>();
                MethodVisitor v = new MethodVisitor(Opcodes.ASM4) {
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                        //参数级别
                        // 静态方法第一个参数就是方法的参数，如果是实例方法，第一个参数是this
                        if (Modifier.isStatic(method.getModifiers())) {
                            parameterList.add(name);
                        } else if (index > 0) {
                            parameterList.add(name);
                        }
                    }
                };
                methods.put(method, parameterList);
                return v;
            }
        }, 0);
        return methods;
    }
}
