package org.bs.db;

import com.bassis.bean.annotation.Component;

@Component
public class UserDb {
    public String add(String name) {
        return name + System.currentTimeMillis() + this.getClass().getSimpleName();
    }
}
