package com.bassis.tools.reflex;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.bassis.tools.exception.CustomException;

/**
 * 代理执行
 */
public class Reflection {
    static String error_str = Reflection.class.getName() + " 代理异常";

    /**
     * 获得class下的某个方法
     *
     * @param declared       是否只获取当前类下的方法
     * @param las            类
     * @param methodName     方法名
     * @param parameterTypes 方法入参类型
     * @return 返回方法对象
     */
    public static Method getMethod(boolean declared, Class<?> las, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        try {
            assert las != null;
            assert methodName != null;

            if (declared) {
                method = las.getDeclaredMethod(methodName, parameterTypes);
            } else {
                method = las.getMethod(methodName, parameterTypes);
            }
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
        }
        return method;
    }

    /**
     * 根据class初始化实例并且代执行方法
     *
     * @param declared    是否只获取当前类下的方法
     * @param las         类
     * @param method_name 方法名
     * @return 返回方法执行后的对象obj
     */
    public static Object invoke(boolean declared, Class<?> las, String method_name) {
        Object obj;
        try {
            obj = las.newInstance();
        } catch (Exception e) {
            CustomException.throwOut("初始化类失败", e);
            return null;
        }
        return invokeMethod(obj, getMethod(declared, las, method_name));
    }

    /**
     * 根据class初始化实例并且代执行方法
     *
     * @param declared    是否只获取当前类下的方法
     * @param las         类
     * @param method_name 方法名
     * @return 返回方法执行后的对象obj
     */
    public static Object invoke(boolean declared, Class<?> las, String method_name, Class<?>... parameterTypes) {
        Object obj;
        try {
            obj = las.newInstance();
        } catch (Exception e) {
            CustomException.throwOut("初始化类失败", e);
            return null;
        }
        return invokeMethod(obj, getMethod(declared, las, method_name, parameterTypes));
    }

    /**
     * 进行代理执行
     *
     * @param obj    要执行的实例
     * @param method 代理的方法
     * @return 返回执行结果
     */
    public static Object invokeMethod(Object obj, Method method) {
        return invokeMethod(obj, method, (Object[]) method.getParameters());
    }

    /**
     * 代理执行方法
     *
     * @param obj    要执行的实例
     * @param method 代理的方法
     * @param args   方法参数
     * @return 返回执行结果
     */
    public static Object invokeMethod(Object obj, Method method, Object... args) {
        try {
            assert obj != null;
            assert method != null;
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
        }
        return null;
    }

    /**
     * 获取接口上的泛型
     *
     * @param aclass 要获取的接口
     * @param index  第几个泛型 默认第一个,下标从1开始
     * @return 返回获取的泛型
     */
    public static Class<?> getInterfaceT(Class<?> aclass, int index) {
        if (index <= 1) index = 0;
        else index--;
        ParameterizedType parameterized;
        try {
            parameterized = (ParameterizedType) aclass.getGenericInterfaces()[index];
        } catch (Exception e) {
            parameterized = (ParameterizedType) aclass.getSuperclass().getGenericInterfaces()[index];
        }
        return (Class<?>) parameterized.getActualTypeArguments()[index];
    }

    /**
     * 获取类上的泛型
     *
     * @param aclass 要获取的类
     * @param index  第几个泛型 默认第一个,下标从1开始
     * @return 返回获取的泛型
     */
    public static Class<?> getClassT(Class<?> aclass, int index) {
        if (index <= 1) index = 0;
        else index--;
        return (Class<?>) ((ParameterizedType) aclass.getGenericSuperclass()).getActualTypeArguments()[index];
    }

    /**
     * 获得一个接口的 所有实现类
     *
     * @param aclass     接口实例
     * @param isAbstract 是否包含抽象类 默认不包含
     * @return 返回所有的实现类
     */
    public static List<Class<?>> getInterfaceImplClass(Class<?> aclass, boolean isAbstract) {
        List<Class<?>> classList = new ArrayList<>();
        ClassLoader classLoader = ReflexUtils.getClassLoader();
        Class<?> classOfClassLoader = classLoader.getClass();
        while (classOfClassLoader != ClassLoader.class) {
            classOfClassLoader = classOfClassLoader.getSuperclass();
        }
        Field field = null;
        try {
            field = classOfClassLoader.getDeclaredField("classes");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        Vector v = null;
        try {
            v = (Vector) field.get(classLoader);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Object aV : v) {
            Class<?> c = (Class<?>) aV;
            if (aclass.isAssignableFrom(c) && !aclass.equals(c) && isAbstract) {
                //包含抽象类
                classList.add(c);
            } else if (aclass.isAssignableFrom(c) && !aclass.equals(c) && !isAbstract && !Modifier.isAbstract(c.getModifiers())) {
                //不包含抽象类
                classList.add(c);
            }
        }
        return classList;
    }
}
