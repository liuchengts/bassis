package com.bassis.bean;

import com.bassis.bean.annotation.Scope;
import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.common.Bean;
import com.bassis.bean.common.enums.ScopeEnum;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.domain.AutowiredEvent;
import com.bassis.bean.event.domain.ResourcesEvent;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.tools.exception.CustomException;
import net.sf.cglib.beans.BeanGenerator;
import org.apache.log4j.Logger;
import com.bassis.bean.annotation.impl.ComponentImpl;

import java.util.*;

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

    private static final ApplicationEventPublisher applicationEventPublisher = ApplicationEventPublisher.getInstance();
    /**
     * 一级缓存：所有bean存储器
     */
    private static final Map<Class<?>, LinkedList<Bean>> objectBeanStorage = new HashMap<>(256);

    /**
     * 二级缓存：存放正在初始化的对象，用于解决循环依赖
     */
    private static final Map<Class<?>, Bean> singletonFactories = new HashMap<>(16);

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
        //发布资源就绪事件
        applicationEventPublisher.publishEvent(new AutowiredEvent(Object.class));
        applicationEventPublisher.publishEvent(new ResourcesEvent(Object.class));
        return getInstance();
    }

    /**
     * 检查class是否带有范围注解 默认为单实例
     *
     * @param aclass 要检测的class
     * @return 单实例为true 多实例为false
     */
    public static boolean isScopeSingleton(Class<?> aclass) {
        return !aclass.isAnnotationPresent(Scope.class) || !aclass.getAnnotation(Scope.class).value().equals(ScopeEnum.PROTOTYPE);
    }

    /**
     * 检查class是否需要创建bean
     *
     * @param aclass 要检测的class
     * @return 需要创建为true 不需要创建为false
     */
    public static boolean isCreateBean(Class<?> aclass) {
        return !isScopeSingleton(aclass) || !objectBeanStorage.containsKey(aclass);
    }

    /**
     * 创建一个bean
     *
     * @param aclass 要创建的class
     * @return 返回创建好的bean
     */
    public synchronized void newBeanTask(Class<?> aclass) {
        if (objectBeanStorage.containsKey(aclass)) {
            //第一阶段，检测一级缓存中是否已存在当前aclass
            //存在bean
        } else if (singletonFactories.containsKey(aclass)) {
            //第二阶段，检测二级缓存中是否已存在当前aclass
            // 存在bean 将当前bean返回
            Bean bean = singletonFactories.get(aclass);
            //将这个bean放入一级缓存
            LinkedList<Bean> beans = new LinkedList<>();
            beans.add(bean);
            //按顺序检测下来一级缓存中不存在，这里直接塞入，并且删除二级缓存
            objectBeanStorage.put(aclass, beans);
            singletonFactories.remove(aclass);
        } else {
            //创建一个待初始化的bean放入二级缓存
            Bean bean = new Bean(ProxyFactory.invoke(aclass), 1);
            singletonFactories.put(aclass, bean);
            //检测资源注入,并且加入事件
            AutowiredImpl autowired = new AutowiredImpl();
            applicationEventPublisher.addListener(autowired);
            autowired.analyseFields(bean.getObject(), true);
        }
    }

    /**
     * 获得一个资源就绪的bean
     *
     * @param aclass 要获得的实例
     * @return 返回获得的bean
     */
    public Bean getBean(Class<?> aclass) {
        Bean bean = null;
        if (objectBeanStorage.containsKey(aclass)) {
            //存在bean 直接返回第一个bean
            bean = objectBeanStorage.get(aclass).getFirst();
        }
        return bean;
    }

    /**
     * 获得一个资源就绪的bean
     *
     * @param name 要获得的实例的别名
     * @return 返回获得的bean
     */
    public Bean getBean(String name) {
        return getBean(ComponentImpl.getBeansClass(name));
    }

    /**
     * 删除一个bean
     * (如果bean没有索引，不会执行bean存储器删除)
     *
     * @param bean 要移除的bean
     * @return 返回成功或失败
     */
    public synchronized boolean removeBean(Bean bean) {
        try {
            assert bean != null;
            assert bean.getObject() != null;
            Class aclass = bean.getObject().getClass();
            //删除bean存储器
            if (!objectBeanStorage.containsKey(aclass)) return false;
            LinkedList<Bean> beans = objectBeanStorage.get(aclass);
            if (beans.isEmpty()) return false;
            if (null != bean.getIndex() && bean.getIndex() > 0) {
                beans.remove(bean.getIndex().intValue());
                objectBeanStorage.put(aclass, beans);
                return true;
            } else {
                logger.warn("bean index is null");
            }
        } catch (Exception e) {
            CustomException.throwOut(" removeBean exception", e);
        }
        return false;
    }


    /**
     * 获得class 创建的实例列表
     *
     * @param aclass 要获得的class
     * @return 返回创建的实例列表
     */
    public LinkedList<Bean> getBeanCount(Class<?> aclass) {
        LinkedList<Bean> beans = new LinkedList<>();
        if (objectBeanStorage.containsKey(aclass)) {
            beans = objectBeanStorage.get(aclass);
        }
        return beans;
    }
}
