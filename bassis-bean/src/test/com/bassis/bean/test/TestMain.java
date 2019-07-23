package com.bassis.bean.test;

import com.bassis.bean.BeanFactory;

public class TestMain {
    public static void main(String[] args) throws Exception {
        BeanFactory beanFactory = BeanFactory.startBeanFactory("com.bassis.bean.test");
        TestProxy testProxy = (TestProxy) beanFactory.getBean(TestProxy.class);
        String res = testProxy.tp();
        System.out.println(res);
    }
}
