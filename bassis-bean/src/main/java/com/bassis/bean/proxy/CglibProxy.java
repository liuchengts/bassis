package com.bassis.bean.proxy;

import com.bassis.bean.annotation.impl.AopImpl;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * 基于cglib类的代理
 */
public class CglibProxy implements MethodInterceptor {
    private static Logger logger = Logger.getLogger(CglibProxy.class);
    //代理执行的方法参数
    private Object[] parameter;
    //代理的class类
    private Class<?> target;
    //代理得到的对象
    private Object obj;

    /**
     * 获得代理的结果
     *
     * @return obj
     */
    public Object getObj() {
        return obj;
    }

    private void setObj(Object obj) {
        this.obj = obj;
    }

    /**
     * 获得代理的class
     *
     * @return target
     */
    public Class<?> getTarget() {
        return target;
    }

    private void setTarget(Class<?> target) {
        this.target = target;
    }

    /**
     * 获得代理执行的方法参数
     *
     * @return parameter
     */
    public Object[] getParameter() {
        return parameter;
    }

    private void setParameter(Object[] parameter) {
        this.parameter = parameter;
    }

    /**
     * 获得一个新的代理对象
     *
     * @return 返回当前类实例
     */
    public static CglibProxy CreateCglibProxy() {
        return new CglibProxy();
    }

    /**
     * 初始化代理
     *
     * @param clazz  要代理的class
     * @param _proxy cglib代理对象
     * @return 返回代理的对象
     */
    @SuppressWarnings("rawtypes")
    public Object getInstance(Class clazz, CglibProxy _proxy) {
        if (null == _proxy) {
            _proxy = new CglibProxy();
        }
        _proxy.setTarget(clazz);
        return _proxy.getProxy(clazz);
    }


    /**
     * 初始化代理 不会生成新的子类字节码）
     *
     * @param obj    代理的源对象对象实例
     * @param _proxy 代理
     * @return 返回代理的对象实例
     */
    @SuppressWarnings("rawtypes")
    public Object getInstance(Object obj, CglibProxy _proxy) {
        if (null == _proxy) {
            _proxy = new CglibProxy();
        }
        _proxy.setTarget(obj.getClass());
        return _proxy.getProxy(obj);
    }

    /***
     * obj 代理（不会生成新的子类字节码）
     * @param obj 要代理的obj对象
     * @return 返回代理的对象实例
     */
    @SuppressWarnings("rawtypes")
    private Object getProxy(Object obj) {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setCallback(this);
        // 通过字节码技术动态创建子类实例
        this.setObj(obj);
        return obj;
    }

    /***
     *  class代理
     * @param clazz 要代理的class
     * @return 返回代理的对象实例
     */
    @SuppressWarnings("rawtypes")
    private Object getProxy(Class clazz) {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        //设置单一回调对象，在调用中拦截对目标方法的调用
        enhancer.setCallback(this);
        //设置类加载器
        enhancer.setClassLoader(clazz.getClassLoader());
        // 通过字节码技术动态创建子类实例
        Object o = enhancer.create();
        this.setObj(o);
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
        // 通过代理类调用父类中的方法
        this.setParameter(objects);
        Object result = null;
        boolean isAop = AopImpl.isAop(method);
        if (isAop) {
            //初始化aop
            AopImpl aop = new AopImpl();
            aop.analyseAop(o, method);
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
                //执行完成方法
                aop.afterCompletion();
            }
        } else {
            result = methodProxy.invokeSuper(o, objects);
        }
        return result;
    }
}
