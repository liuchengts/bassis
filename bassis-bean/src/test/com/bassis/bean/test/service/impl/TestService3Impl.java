package com.bassis.bean.test.service.impl;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.annotation.Scope;
import com.bassis.bean.common.enums.ScopeEnum;
import com.bassis.bean.test.service.TestService1;
import com.bassis.bean.test.service.TestService3;

@Scope(value = ScopeEnum.PROTOTYPE)
@Component
public class TestService3Impl implements TestService3 {
//    @Autowired(aclass = TestService1Impl.class)
    @Autowired
    TestService1 testService1;

    public String out() {
        String str = "TestService3";
        System.out.println(str + testService1.out());
        return str;
    }
    public String out2() {
        String str = "TestService3";
        System.out.println(str );
        return str;
    }
}
