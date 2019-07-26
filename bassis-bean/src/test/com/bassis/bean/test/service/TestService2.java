package com.bassis.bean.test.service;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;

@Component
public class TestService2 {
    @Autowired(aclass = TestService3.class)
    TestService3 testService3;

    public String out() {
        String str = "TestService2";
        System.out.println(str + testService3.out2());
        return str;
    }
}
