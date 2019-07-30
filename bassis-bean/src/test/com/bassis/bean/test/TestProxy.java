package com.bassis.bean.test;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;


@Component
public class TestProxy {
    @Autowired(aclass = TestComponent.class)
    TestComponent testComponent;

    public String tp() {
        return testComponent.tc("",null);
    }
}
