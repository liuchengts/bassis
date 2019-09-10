package com.bassis.bean.test;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.common.Bean;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.domain.AutowiredEvent;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.bean.test.service.TestService1;
import com.bassis.bean.test.service.TestService2;
import com.bassis.bean.test.service.TestService3;
import com.bassis.bean.test.service.TestService4;
import com.bassis.bean.test.service.impl.TestService1Impl;
import com.bassis.bean.test.service.impl.TestService2Impl;
import com.bassis.bean.test.service.impl.TestService3Impl;
import com.bassis.tools.reflex.ReflexUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TestMain {
    static BeanFactory beanFactory;

    public static void main(String[] args) throws Exception {
        //启动 BeanFactory
        beanFactory = BeanFactory.startBeanFactory("com.bassis.bean.test");
        testCreateBean();
//        testForAutowired();
//        testAop();
//        testApplicationEvent();
//        testCopyBean();
    }

    private static void testCreateBean() {
        //TODO bug：多例模式下产生的bean问题
        //TODO 1、第二次的对象获取是直接获得的一级缓存，应该是重新创建一个bean
        //TODO 2、第三次进行了重新创建注入 但是没有走注入以及注入检测过程，直接从一级缓存中获得了一个未注入属性的对象，引发了第三次调用的属性为null的问题
        int i = 0;
        while (i < 3) {
            TestService4 service4 = (TestService4) beanFactory.createBean(TestService4.class).getObject();
            service4.out();
            i++;
        }
    }

    private static void testAop() {
        TestProxy testProxy = (TestProxy) beanFactory.createBean(TestProxy.class).getObject();
        String res = testProxy.tp();
        System.out.println(res);
    }

    private static void testApplicationEvent() {
        AutowiredImpl autowired = new AutowiredImpl();
        ApplicationEventPublisher.addListener(autowired);
        ApplicationEventPublisher.publishEvent(new AutowiredEvent(TestService1.class));

    }

    private static void testForAutowired() {
        TestService1 service1 = (TestService1) beanFactory.createBean(TestService1.class).getObject();
        service1.out();
        TestService2 service2 = (TestService2) beanFactory.createBean(TestService2.class).getObject();
        service2.out();
        TestService3 service3 = (TestService3) beanFactory.createBean(TestService3.class).getObject();
        service3.out2();

        System.out.println(beanFactory.getBeanList(TestService1.class).size());
        System.out.println(beanFactory.getBeanList(TestService2.class).size());
        System.out.println(beanFactory.getBeanList(TestService3.class).size());
        //TestService3 是多实例模式，所以在这里打印其object地址
        beanFactory.getBeanList(TestService3.class).forEach(bean -> {
            System.out.println("bean:" + bean.getObject().toString());
            System.out.println("bean:" + bean.getIndex());
            System.out.println("bean:" + bean.getCv());
            System.out.println("bean:" + bean.getOv());
        });

    }

    private static void testCopyBean() {
        Bean source = new Bean(ProxyFactory.invoke(TestService1.class), 1);
        Bean target = new Bean();
        beanFactory.copyBean(source, target);
    }
}
