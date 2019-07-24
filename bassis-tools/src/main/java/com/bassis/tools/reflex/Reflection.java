package com.bassis.tools.reflex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}
