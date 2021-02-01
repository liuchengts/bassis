package com.bassis.bean.boot.test.aop;

import com.bassis.bean.annotation.Component;
import com.bassis.bean.aop.AopService;
//import org.apache.log4j.Logger;

@Component(name = "userAopService")
public class UserAopServiceImpl implements AopService {
//    static Logger logger = Logger.getLogger(UserAopServiceImpl.class);

    @Override
    public boolean preHandle(Object... objs) {
//        logger.info("preHandle");
        for (Object obj : objs) {
//            logger.info("aop入参:" + obj.toString());
        }
        return true;
    }

    @Override
    public void postHandle(Object... objs) {
        //logger.info("postHandle");
    }

    @Override
    public void afterCompletion(Object... objs) {
        //logger.info("afterCompletion");
    }
}
