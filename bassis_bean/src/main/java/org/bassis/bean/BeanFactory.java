package org.bassis.bean;

import org.apache.log4j.Logger;
import org.bassis.bean.annotation.impl.ComponentImpl;

/**
 * bean工厂
 */
public class BeanFactory {

    static Logger logger = Logger.getLogger(BeanFactory.class);

    private static class LazyHolder {
        private static final BeanFactory INSTANCE = new BeanFactory();
    }

    private BeanFactory() {
    }

    public static final BeanFactory getInstance() {
        BeanFactory beanFactory = BeanFactory.LazyHolder.INSTANCE;
        beanFactory.init();
        return beanFactory;
    }

    ComponentImpl component;

    private void init() {
        //component扫描
        component = ComponentImpl.getInstance();
    }
}
