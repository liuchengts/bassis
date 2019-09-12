package com.bassis.bean.proxy;

import com.bassis.bean.annotation.impl.AopImpl;
import com.bassis.bean.annotation.impl.ComponentImpl;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 基于cglib类的代理
 */
public class CglibProxy implements MethodInterceptor {
    private static Logger logger = Logger.getLogger(CglibProxy.class);
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
    public static CglibProxy createCglibProxy() {
        return new CglibProxy();
    }

    /**
     * 初始化代理
     *
     * @param clazz 要代理的class
     * @return 返回代理的对象
     */
    @SuppressWarnings("rawtypes")
    public static Object getInstance(Class clazz) {
        CglibProxy _proxy = new CglibProxy();
        _proxy.setTarget(clazz);
        _proxy.setBeanAopMethods(ComponentImpl.getClassAopMethods(_proxy.getTarget()));
        return _proxy.newProxy();
    }


    /**
     * 初始化代理 不会生成新的子类字节码
     *
     * @param obj 代理的源对象对象实例
     * @return 返回代理的对象实例
     */
    @SuppressWarnings("rawtypes")
    public static Object getInstance(Object obj) {
        CglibProxy _proxy = new CglibProxy();
        _proxy.setTarget(obj.getClass());
        _proxy.setBeanAopMethods(ComponentImpl.getClassAopMethods(_proxy.getTarget()));
        return _proxy.newProxy();
    }

    /***
     * obj 代理（不会生成新的子类字节码）
     * @return 返回代理的对象实例
     */
    @SuppressWarnings("rawtypes")
    private Object newProxyObject() {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setCallback(this);
        // 通过字节码技术动态创建子类实例
        this.setProxy(this.proxy);
        return this.proxy;
    }

    /***
     *  class代理
     * @return 返回代理的对象实例
     */
    @SuppressWarnings("rawtypes")
    private Object newProxy() {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(this.target);
        //设置单一回调对象，在调用中拦截对目标方法的调用
        enhancer.setCallback(this);
        // 通过字节码技术动态创建子类实例
        Object o = enhancer.create();
        this.setProxy(o);
        return o;
    }


    /***
     * 实现MethodInterceptor接口方法
     * @param o 执行对象实例
     * @param method 方法
     * @param objects 方法执行参数
     * @param methodProxy  代理类
     * @return 返回代理执行结果
     * @throws Throwable 异常抛出
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        boolean isAop = this.getBeanAopMethods().contains(method);
        if (isAop) {
            //初始化aop
            AopImpl aop = new AopImpl();
            aop.analyseAop(o, method, objects);
            try {
                //执行前置方法
                if (aop.preHandle()) {
                    //执行当前方法
                    result = methodProxy.invokeSuper(o, objects);
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
            result = methodProxy.invokeSuper(o, objects);
        }
        return result;
    }

}
