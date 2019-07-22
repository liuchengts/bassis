package org.bassis.bean.boot;

import org.bassis.boot.application.BassisApplication;

public class TestApplication {
    public static void main(String[] args) throws Exception {
        BassisApplication.run(TestApplication.class,args);
    }

}
