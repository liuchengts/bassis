package org.bassis.bassis_tools.reflex;

import org.bassis.bassis_tools.exception.CustomException;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ReflexUtils {
    static Set<Class<?>> basisTypes = new HashSet<>();
    static Set<Class<?>> basis_pack_Types = new HashSet<>();
    static ClassLoader classLoader;
    static Thread thread;
    static String error_str = ReflexUtils.class.getName() + "类型异常";

    static {
        basis_pack_Types.add(Date.class);
        basis_pack_Types.add(Integer.class);
        basis_pack_Types.add(String.class);
        basis_pack_Types.add(Double.class);
        basis_pack_Types.add(Float.class);
        basis_pack_Types.add(Boolean.class);
        basis_pack_Types.add(Long.class);
        basis_pack_Types.add(Byte.class);
        basis_pack_Types.add(File.class);
        //==基础数据类型
        basisTypes.add(int.class);
        basisTypes.add(double.class);
        basisTypes.add(float.class);
        basisTypes.add(boolean.class);
        basisTypes.add(long.class);
        basisTypes.add(byte.class);
        basisTypes.add(short.class);
        thread = Thread.currentThread();
        classLoader = thread.getContextClassLoader();
    }

    /**
     * 获得当前线程
     *
     * @return 当前线程
     */
    public static Thread getThread() {
        return thread;
    }

    /**
     * 获得类加载器
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * 检查是否是基础数据类型（包装类型）
     *
     * @param clz 类
     * @return 只有当不是基础数据类型返回false
     */
    public static boolean isWrapClass(Class<?> clz) {
        boolean fag = basisTypes.contains(clz);
        if (!fag) {
            fag = isWrapClass_Pack(clz);
        }
        return fag;
    }

    /**
     * 检查是否是基础数据包装类
     *
     * @param clz 类
     * @return 只有当不是基础数据类型包装类返回false
     */
    public static boolean isWrapClass_Pack(Class<?> clz) {
        return basis_pack_Types.contains(clz);
    }

    /**
     * 检查是否是基础数据类型（包装类型）
     *
     * @param path 类型包路径（会使用加载器进行类加载）
     * @return 只有当不是基础数据类型返回false
     */
    public static boolean isWrapClass(String path) {
        try {
            return isWrapClass(classLoader.loadClass(path));
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return true;
        }
    }

    /**
     * 检查是否是基础数据包装类
     *
     * @param path 类型包路径（会使用加载器进行类加载）
     * @return 只有当不是基础数据类型装类返回false
     */
    public static boolean isWrapClass_Pack(String path) {
        try {
            return isWrapClass_Pack(classLoader.loadClass(path));
        } catch (Exception e) {
            CustomException.throwOut(error_str, e);
            return true;
        }
    }


    /**
     * 检查是否存在class
     *
     * @param path 类型包路径（会使用加载器进行类加载）
     * @return true表示存在，并且已经加载进内存
     */
    public static boolean isClass(String path) {
        try {
            classLoader.loadClass(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
