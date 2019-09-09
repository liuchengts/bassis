package com.bassis.bean.test.service.impl;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.annotation.Scope;
import com.bassis.bean.common.enums.ScopeEnum;
import com.bassis.bean.test.TestComponent;
import com.bassis.bean.test.service.TestService1;
import com.bassis.bean.test.service.TestService3;
import com.bassis.bean.test.service.TestService4;
import org.apache.log4j.Logger;

@Scope(value = ScopeEnum.PROTOTYPE)
@Component
public class TestService4Impl implements TestService4 {
    static Logger logger = Logger.getLogger(TestService4Impl.class);
    @Autowired
    TestComponent testComponent;

    public String out() {
        String str = "TestService4";
        str = testComponent.tc(str, "11111");
        logger.info(str);
        return str;
    }
}
