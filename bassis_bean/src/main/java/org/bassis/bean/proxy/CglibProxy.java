package org.bassis.bean.proxy;

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
     * @param clazz
     * @param _proxy
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Object getInstance(Class clazz, CglibProxy _proxy) {
        if (null == _proxy) {
            _proxy = new CglibProxy();
        }
        _proxy.setTarget(clazz);
        return _proxy.getProxy(clazz);
    }

    @SuppressWarnings("rawtypes")
    private Object getProxy(Class clazz) {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        // 通过字节码技术动态创建子类实例
        Object o = enhancer.create();
        this.setObj(o);
        return o;
    }

    /**
     * obj 方式的代理不会生成新的子类字节码
     *
     * @param obj 代理的源对象对象实例
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
     * 实现MethodInterceptor接口方法
     * @param obj 执行对象实例
     * @param method 方法
     * @param args 方法执行参数
     * @param proxy  代理类
     * @return 返回代理执行结果
     * @throws Throwable 异常抛出
     */

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 通过代理类调用父类中的方法
        this.setParameter(args);
        Object result = proxy.invokeSuper(obj, args);
        method.invoke(result, args);
        return result;
    }

}
