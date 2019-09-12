package com.bassis.bean.test;

import com.bassis.bean.annotation.Aop;
import com.bassis.bean.annotation.Component;
import org.apache.log4j.Logger;

@Component
public class TestComponent {
    static Logger logger = Logger.getLogger(TestComponent.class);

    @Aop(aclass = TestAopServiceImpl.class, parameters = {"a", "b", "c"})
    public String tc(String name,String ip) {
        if (null == name) name = "当前入参是null";
        logger.info("tc");
        return name + System.currentTimeMillis();
    }
}
