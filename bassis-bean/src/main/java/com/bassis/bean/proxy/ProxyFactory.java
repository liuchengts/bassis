package com.bassis.bean.proxy;

import org.apache.log4j.Logger;

/**
 * 代理工厂类
 */
public class ProxyFactory {
    private static Logger logger = Logger.getLogger(ProxyFactory.class);

    private static class LazyHolder {
        private static final ProxyFactory INSTANCE = new ProxyFactory();
    }

    private ProxyFactory() {
    }

    public static final ProxyFactory getInstance() {
        return LazyHolder.INSTANCE;
    }


    /**
     * 代理模式  默认使用cglib代理 jdk代理模式只能代理接口
     *
     * @param target 要代理的class
     * @return 返回代理对象
     */
    public static Object invoke(Class target) {
        Object factory = target;
        // 判断接口与类
        if (target.isInterface()) {
            // 接口 调用jdk工厂
            logger.debug("使用jdk工厂");
            factory = JDKProxy.getInstance(target);
        } else {
            factory = cglibInvoke(target);
        }
        return factory;
    }

    /**
     * 代理模式 使用cglib工厂代理 不能代理final修饰符的方法
     *
     * @param target 代理的class
     * @return 返回代理对象
     */
    public static Object cglibInvoke(Class target) {
        Object factory = target;
        logger.debug("使用cglib工厂");
        CglibProxy proxy = CreateCglibProxy();
        factory = proxy.getInstance(target, proxy);
        return factory;
    }

    /**
     * 获得一个cglib代理对象
     */
    public static CglibProxy CreateCglibProxy() {
        return CglibProxy.CreateCglibProxy();
    }

    /**
     * 代理模式 使用cglib工厂代理 不能代理final修饰符的方法
     *
     * @param obj
     * @return
     */
    public static Object invoke(Object obj, CglibProxy proxy) {
        Object factory = obj;
        logger.debug("使用cglib工厂");
        factory = proxy.getInstance(obj, proxy);
        return factory;
    }

}
