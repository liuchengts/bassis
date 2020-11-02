package com.bassis.bean.test.service.impl;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.test.service.TestService1;
import com.bassis.bean.test.service.TestService2;

@Component
public class TestService1Impl implements TestService1 {
    //    @Autowired(aclass = TestService2Impl.class)
    @Autowired
    TestService2 testService2;

    public String out() {
        String str = "TestService1";
        System.out.println(str + testService2.out());
        return str;
    }
}
