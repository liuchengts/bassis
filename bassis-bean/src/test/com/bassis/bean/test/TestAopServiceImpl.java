package com.bassis.bean.test;

import com.bassis.bean.annotation.Component;
import com.bassis.bean.aop.AopService;
import org.apache.log4j.Logger;

@Component
public class TestAopServiceImpl implements AopService {
    static Logger logger = Logger.getLogger(TestAopServiceImpl.class);

    @Override
    public boolean preHandle(Object... objs) {
        logger.info("preHandle");
        return true;
    }

    @Override
    public void postHandle(Object... objs) {
        logger.info("postHandle");
    }

    @Override
    public void afterCompletion(Object... objs) {
        logger.info("afterCompletion");
    }
}
