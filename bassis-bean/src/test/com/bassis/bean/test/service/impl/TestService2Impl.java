package com.bassis.bean.test.service.impl;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.test.service.TestService2;
import com.bassis.bean.test.service.TestService3;

@Component
public class TestService2Impl implements TestService2 {
    @Autowired(aclass = TestService3Impl.class)
    TestService3 testService3;

    public String out() {
        String str = "TestService2";
        System.out.println(str + testService3.out2());
        return str;
    }
}
