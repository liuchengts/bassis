package org.bassis.bassis_tools.reflex;

import java.lang.reflect.Method;

import org.bassis.bassis_tools.exception.CustomException;

/**
 * 代理执行
 */
public class Reflection {
    static String error_str = Reflection.class.getName() + "代理异常";

    /**
     * 获得class下的某个方法
     *
     * @param las            类
     * @param methodName     方法名
     * @param parameterTypes 方法入参类型
     * @return 返回方法对象
     */
    public static Method getMethod(Class<?> las, String methodName, Class<?>... parameterTypes) {
        try {
            assert las != null;
            assert methodName != null;
            return las.getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }

    /**
     * 根据方法名获得class下的某个方法
     *
     * @param las        类
     * @param methodName 方法名
     * @return 返回方法对象
     */
    public static Method getMethod(Class<?> las, String methodName) {
        try {
            assert las != null;
            assert methodName != null;
            Method[] methods = las.getDeclaredMethods();
            Method method = null;
            // 方法筛选
            for (Method m : methods) {
                if (m.getName().equals(methodName)) {
                    method = m;
                    break;
                }
            }
            return method;
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }

    /**
     * 根据class初始化实例并且代执行方法
     *
     * @param las         类
     * @param method_name 方法名
     * @return 返回方法执行后的对象obj
     */
    public static Object invoke(Class<?> las, String method_name) {
        Object obj;
        try {
            obj = las.newInstance();
        } catch (Exception e) {
            CustomException.throwOut("初始化类失败", e);
            return null;
        }
        return invokeMethod(obj, getMethod(las, method_name));
    }

    /**
     * 根据class初始化实例并且代执行方法
     *
     * @param las         类
     * @param method_name 方法名
     * @return 返回方法执行后的对象obj
     */
    public static Object invoke(Class<?> las, String method_name, Class<?>... parameterTypes) {
        Object obj;
        try {
            obj = las.newInstance();
        } catch (Exception e) {
            CustomException.throwOut("初始化类失败", e);
            return null;
        }
        return invokeMethod(obj, getMethod(las, method_name, parameterTypes));
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
            return method.invoke(obj, args);
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return null;
        }
    }
}
