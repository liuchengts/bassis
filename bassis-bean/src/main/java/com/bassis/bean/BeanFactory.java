package com.bassis.bean;

import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.common.Bean;
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

    /**
     * 所有bean存储器
     */
    Map<Class, LinkedList<Bean>> objectBeanStorage = new HashMap<>();

    /**
     * 自动注入bean的关系
     */
    Map<Class, Set<Class>> autowiredBeanTree = new LinkedHashMap<>();

    /**
     * s
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
        //代理生成
        Object object = this.newBean(aclass);
        //自动注入资源
        AutowiredImpl.analyseFields(object, true);
        return object;
    }

    /**
     * 创建一个bean
     *
     * @param aclass 要创建的bean  class
     * @return 返回创建的bean对象
     */
    public synchronized Bean newBean(Class aclass) {
        LinkedList<Bean> beans;
        if (objectBeanStorage.containsKey(aclass)) {
            beans = objectBeanStorage.get(aclass);
        } else {
            beans = new LinkedList<>();
        }
        Bean bean = new Bean(ProxyFactory.invoke(aclass), beans.size() + 1);
        beans.add(bean);
        objectBeanStorage.put(aclass, beans);
        return bean;
    }

    /**
     * 创建一个autowired需要的bean
     *
     * @param oclass 调用注入的class
     * @param aclass 要注入的对象  class
     * @return 返回创建的bean对象
     */
    public synchronized Bean newAutowiredBean(Class oclass, Class aclass) {
        LinkedList<Bean> beans;
        Bean bean;
        //a -> b  只有当aclass中已存在当前oclass对象，即为循环依赖
        //a -> b -> c -> a 追踪链
        if (autowiredBeanTree.containsKey(aclass)
                && autowiredBeanTree.get(aclass).contains(oclass)) {
            CustomException.throwOut(oclass.getName() + " autowired bean Circular dependencies" + aclass.getName());
        }
        //TODO 这里要实现注入追踪链方法
        if (objectBeanStorage.containsKey(aclass)) {
            beans = objectBeanStorage.get(aclass);
        } else {
            beans = new LinkedList<>();
        }
        if (!beans.isEmpty()) {
            bean = beans.getFirst();
        } else {
            bean = new Bean(ProxyFactory.invoke(aclass), beans.size() + 1);
            //加入bean存储器
            beans.add(bean);
            objectBeanStorage.put(aclass, beans);
        }
        //加入autowired依赖关系
        Set<Class> classBeans;
        if (autowiredBeanTree.containsKey(oclass)) {
            classBeans = autowiredBeanTree.get(oclass);
        } else {
            classBeans = new HashSet<>();
        }
        classBeans.add(aclass);
        autowiredBeanTree.put(oclass, classBeans);
        return bean;
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
}
