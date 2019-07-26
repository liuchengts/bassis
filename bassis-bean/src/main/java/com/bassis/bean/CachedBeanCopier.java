package com.bassis.bean;

import com.bassis.bean.common.Bean;
import net.sf.cglib.beans.BeanCopier;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * cglib obj对象拷贝器
 */
public class CachedBeanCopier {
    private static Logger logger = Logger.getLogger(BeanFactory.class);

    private static class LazyHolder {
        private static final CachedBeanCopier INSTANCE = new CachedBeanCopier();
    }

    private CachedBeanCopier() {
    }

    public static final CachedBeanCopier getInstance() {
        return CachedBeanCopier.LazyHolder.INSTANCE;
    }

    public static final Map<String, BeanCopier> BEAN_COPIERS = new ConcurrentHashMap<>();

    /**
     * 拷贝器
     *
     * @param source           源对象
     * @param target           目标对象
     * @param sourceSuperClass 是否提取 source父类的class
     * @param targetSuperClass 是否提取 target父类的class
     */
    public static void copy(Object source, Object target, boolean sourceSuperClass, boolean targetSuperClass) {
        Class<?> sourceClass;
        Class<?> targetClass;
        if (sourceSuperClass) {
            sourceClass = source.getClass().getSuperclass();
        } else {
            sourceClass = source.getClass();
        }
        if (targetSuperClass) {
            targetClass = target.getClass().getSuperclass();
        } else {
            targetClass = target.getClass();
        }
        String key = genKey(sourceClass, targetClass);
        BeanCopier copier = null;
        if (!BEAN_COPIERS.containsKey(key)) {
            copier = BeanCopier.create(sourceClass, targetClass, false);
            BEAN_COPIERS.put(key, copier);
        } else {
            copier = BEAN_COPIERS.get(key);
        }
        copier.copy(source, target, null);
    }

    /**
     * 计算key
     *
     * @param source 源对象class
     * @param target 目标对象class
     * @return
     */
    private static String genKey(Class<?> source, Class<?> target) {
        return source.getName() + target.getName();
    }
}
