package com.bassis.bean;

import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.tools.exception.CustomException;
import net.sf.cglib.beans.BeanGenerator;
import org.apache.log4j.Logger;
import com.bassis.bean.annotation.impl.ComponentImpl;

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
        return BeanFactory.LazyHolder.INSTANCE;
    }

    /**
     * 启动 BeanFactory
     *
     * @param scanPath 扫描起点
     * @return 返回 BeanFactory
     */
    public static BeanFactory startBeanFactory(String scanPath) {
        Scanner.startScan(scanPath);
        //component扫描
        ComponentImpl.getInstance();
        return getInstance();
    }

    /**
     * 获得一个资源就绪的bean
     *
     * @param aclass 要获得的实例
     * @return 返回获得的bean
     */
    public Object getBean(Class aclass) {
        return matchBean(ComponentImpl.getBeansClass(aclass));
    }

    /**
     * 获得一个资源就绪的bean
     *
     * @param name 要获得的实例的别名
     * @return 返回获得的bean
     */
    public Object getBean(String name) {
        return matchBean(ComponentImpl.getBeansClass(name));
    }

    /**
     * 匹配bean
     *
     * @param aclass 实例
     * @return 返回获得的bean
     */
    private Object matchBean(Class aclass) {
        if (null == aclass) CustomException.throwOut(aclass.getName() + "@Component is null：");
        //代理生成
        Object object = ProxyFactory.invoke(aclass);
        //自动注入资源
        AutowiredImpl.analyseFields(object, true);
        return object;
    }


}
