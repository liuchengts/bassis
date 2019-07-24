package com.bassis.bean.test;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.proxy.CglibProxy;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.tools.reflex.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestMain {
    public static void main(String[] args) throws Exception {
        BeanFactory beanFactory = BeanFactory.startBeanFactory("com.bassis.bean.test");
        TestProxy testProxy = (TestProxy) beanFactory.getBean(TestProxy.class);
        String res = testProxy.tp();
        System.out.println(res);
    }
}
