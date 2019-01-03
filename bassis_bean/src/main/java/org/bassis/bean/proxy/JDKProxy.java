package org.bassis.bean.proxy;


import org.bassis.tools.exception.CustomException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于接口的代理
 *
 * @author ytx
 */
public class JDKProxy implements InvocationHandler {
    private static Class target;

    /**
     * 不允许直接实例化
     */
    private JDKProxy() {
    }

    /***
     * 初始化调用对象代理
     * @param aclass 要代理的class类
     * @return 返回代理的obj对象
     */
    public static Object getInstance(Class aclass) {
        try {
            JDKProxy pm = new JDKProxy();
            pm.target = aclass;// 赋值,设置这个代理对象
            // 通过Proxy的方法创建代理对象，第一个参数是要代理对象的ClassLoader装载器
            // 第二个参数是要代理对象实现的所有接口
            // 第三个参数是实现了InvocationHandler接口的对象
            // 此时的result就是一个代理对象，代理的是aclass
            return Proxy.newProxyInstance(aclass.getClass().getClassLoader(), aclass.getInterfaces(), pm);
        } catch (Exception e) {
            CustomException.throwOut("代理对象创建失败", e);
        }
        return null;
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
        return method.invoke(target, args);
    }
}
