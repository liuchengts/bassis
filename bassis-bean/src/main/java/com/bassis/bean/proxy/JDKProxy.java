package com.bassis.bean.proxy;


import com.bassis.bean.annotation.impl.AopImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * 基于接口的代理
 */
public class JDKProxy implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(CglibProxy.class);
    //代理的class类
    private Class<?> target;
    //代理得到的对象
    private Object proxy;
    //带aop的方法
    private Set<Method> beanAopMethods = new HashSet<>();

    public Set<Method> getBeanAopMethods() {
        return beanAopMethods;
    }

    public void setBeanAopMethods(Set<Method> beanAopMethods) {
        this.beanAopMethods = beanAopMethods;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    /**
     * 获得一个新的代理对象
     *
     * @return 返回当前类实例
     */
    public static JDKProxy createJDKProxy() {
        return new JDKProxy();
    }

    /***
     * 初始化代理对象
     * @param aclass 要代理的class类
     * @return 返回代理的obj对象
     */
    public static Object getInstance(Class aclass) {
        JDKProxy jdkProxy = new JDKProxy();
        jdkProxy.target = aclass;// 赋值,设置这个代理对象
        return jdkProxy.newProxy();
    }

    /**
     * 获得新的代理对象
     *
     * @return 代理对象
     */
    private Object newProxy() {
        // 通过Proxy的方法创建代理对象，第一个参数是要代理对象的ClassLoader装载器
        // 第二个参数是要代理对象实现的所有接口
        // 第三个参数是实现了InvocationHandler接口的对象
        // 此时的 obj 就是一个代理对象，代理的是aclass
        Object obj = Proxy.newProxyInstance(this.target.getClassLoader(), this.target.getInterfaces(), this);
        this.setProxy(obj);
        return obj;
    }

    /**
     * 代理调用方法
     *
     * @param proxy  被代理的对象 由 getInstance 方法得到
     * @param method 要调用的方法
     * @param args   方法调用时所需要的参数
     * @return 返回方法 method 执行的结果
     * @throws Throwable 异常抛出
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        boolean isAop = this.getBeanAopMethods().contains(method);
        if (isAop) {
            //初始化aop
            AopImpl aop = new AopImpl();
            aop.analyseAop(proxy, method, args);
            try {
                //执行前置方法
                if (aop.preHandle()) {
                    //执行当前方法
                    result = method.invoke(target, args);
                    //执行后置方法
                    aop.postHandle();
                } else {
                    //执行完成方法
                    aop.afterCompletion();
                }
            } catch (Exception e) {
                logger.error("aop exception", e);
                //执行完成方法
                aop.afterCompletion();
            }
        } else {
            result = method.invoke(target, args);
        }
        return result;
    }
}
