package com.bassis.bean.test.service.impl;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.common.enums.ScopeEnum;
import com.bassis.bean.test.TestComponent;
import com.bassis.bean.test.service.TestService4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(scope = ScopeEnum.PROTOTYPE)
public class TestService4Impl implements TestService4 {
    static Logger logger = LoggerFactory.getLogger(TestService4Impl.class);
    @Autowired
    TestComponent testComponent;

    public String out() {
        String str = "TestService4";
        str = testComponent.tc(str, "11111");
        logger.info(str);
        return str;
    }
}
