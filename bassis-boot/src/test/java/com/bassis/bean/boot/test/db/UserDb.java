package com.bassis.bean.boot.test.db;

import com.bassis.bean.annotation.Component;

@Component
public class UserDb {

    public String add(String name) {
        return name + System.currentTimeMillis();
    }
}
