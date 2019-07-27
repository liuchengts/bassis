package org.bs.aop;

import com.bassis.bean.annotation.Component;
import com.bassis.bean.aop.AopService;
import org.apache.log4j.Logger;

@Component
public class UserAop implements AopService {
    static Logger logger = Logger.getLogger(UserAop.class);

    @Override
    public boolean preHandle(Object... objs) {
        logger.info("执行了preHandle :" + objs);
        return true;
    }

    @Override
    public void postHandle(Object... objs) {
        logger.info("执行了postHandle:" + objs);
    }

    @Override
    public void afterCompletion(Object... objs) {
        logger.info("执行了afterCompletion:" + objs);
    }
}
