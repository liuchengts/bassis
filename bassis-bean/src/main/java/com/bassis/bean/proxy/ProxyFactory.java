package com.bassis.bean.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代理工厂类
 */
public class ProxyFactory {
    private static Logger logger = LoggerFactory.getLogger(ProxyFactory.class);

    /**
     * 代理模式
     * 接口使用jdk代理，其他使用cglib代理
     *
     * @param target 要代理的class
     * @return 返回代理对象
     */
    public static Object invoke(Class<?> target) {
        Object factory;
        // 判断接口与类
        if (target.isInterface()) {
            // 接口 调用jdk工厂
            logger.debug("使用jdk工厂 " + target.getName());
            factory = JDKProxy.getInstance(target);
        } else {
            logger.debug("使用cglib工厂 " + target.getName());
            factory = CglibProxy.getInstance(target);
        }
        return factory;
    }

    /**
     * 获得一个jdk代理对象
     *
     * @return jdk代理工厂
     */
    public static JDKProxy createJDKProxy() {
        return JDKProxy.createJDKProxy();
    }

    /**
     * 获得一个jdk代理对象
     *
     * @return cglib代理工厂
     */
    public static CglibProxy createCglibProxy() {
        return CglibProxy.createCglibProxy();
    }

    /**
     * 代理模式 使用cglib工厂代理 不能代理final修饰符的方法
     *
     * @param obj 已经实例化的类
     * @return 代理对象
     */
    public static Object invoke(Object obj) {
        return CglibProxy.getInstance(obj);
    }

}
