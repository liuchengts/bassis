package org.bs.service.impl;

import com.bassis.bean.annotation.Aop;
import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.annotation.Component;
import org.bs.db.UserDb;
import org.bs.service.UserService;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserDb userDb;

    //        @Aop(aclass = UserAopServiceImpl.class)
//        @Aop(aclass = UserAopServiceImpl.class, parameters = {"a", "b", "c"})
//    @Aop(value = "userAopService", parameters = {"a", "b", "c"})
    @Aop(value = "userAopService")
    @Override
    public String add(String name) {
        return userDb.add(name) + this.getClass().getSimpleName();
    }
}
