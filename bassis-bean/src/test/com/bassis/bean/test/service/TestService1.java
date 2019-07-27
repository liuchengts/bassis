package com.bassis.bean.test.service;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;

@Component
public class TestService1 {
    @Autowired(aclass = TestService2.class)
    TestService2 testService2;

    public String out() {
        String str = "TestService1";
        System.out.println(str + testService2.out());
        return str;
    }
}
