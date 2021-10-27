package com.bassis.bean;

import com.bassis.bean.annotation.impl.AutowiredImpl;
import com.bassis.bean.common.Bean;
import com.bassis.bean.common.enums.ScopeEnum;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.domain.AutowiredEvent;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.properties.FileProperties;
import com.bassis.tools.reflex.Reflection;
import com.bassis.bean.annotation.impl.ComponentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * bean工厂
 */
public class BeanFactory {

    private static Logger logger = LoggerFactory.getLogger(BeanFactory.class);
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition STOP = LOCK.newCondition();
    /**
     * 初始化读取器
     */
    static FileProperties fileProperties = FileProperties.getInstance();


    private static class LazyHolder {
        private static final BeanFactory INSTANCE = new BeanFactory();
    }

    private BeanFactory() {
    }

    public static BeanFactory getInstance() {
        return BeanFactory.LazyHolder.INSTANCE;
    }

    /**
     * 一级缓存：所有bean存储器
     */
    private static final Map<Class<?>, LinkedList<Bean>> objectBeanStorage = new ConcurrentHashMap<>(256);

    /**
     * 二级缓存：存放正在初始化的对象，用于解决循环依赖
     */
    private static final Map<Class<?>, Bean> singletonFactories = new ConcurrentHashMap<>(16);

    public static final String CGLIB_TAG = "$$EnhancerByCGLIB$$";
    static AutowiredImpl autowired;

    static {
        //初始化bean拷贝器
        CachedBeanCopier.getInstance();
        //初始化全局事件组件
        ApplicationEventPublisher.getInstance();
        autowired = AutowiredImpl.getInstance();
    }

    /**
     * 启动 BeanFactory
     *
     * @param scanPath 扫描起点
     * @return 返回 BeanFactory
     */
    public static BeanFactory startBeanFactory(String scanPath) {
        //初始化扫描器
        Scanner.startScan(scanPath);
        //开始component扫描
        ComponentImpl.getInstance();
        //将剩下没有循环依赖的bean放入存储器
        getInstance().addBeanSingletonFactories();
        ApplicationEventPublisher.addListener(autowired);
        //发布资源就绪事件
        ApplicationEventPublisher.publishEvent(new AutowiredEvent(Object.class));
        return getInstance();
    }

    /**
     * 从配置文件启动 BeanFactory
     *
     * @return 返回 BeanFactory
     */
    public static BeanFactory startBeanFactory() {
        return startBeanFactory(fileProperties.getProperty("bassis.scan.root"));
    }

    /**
     * 关闭 BeanFactory
     *
     * @param millis 延迟关闭的毫秒数
     */
    public static void stopBeanFactory(long millis) {
        //todo 发布资源准备关闭事件
        blockStop(millis);
    }

    /**
     * 根据bean名称获得Class
     *
     * @param name bean别名
     * @return 返回class
     */
    public Class<?> getBeansClass(String name) {
        return ComponentImpl.getClassByName(name);
    }

    /**
     * 根据beanClass获得Class
     *
     * @param clz bean的class
     * @return 返回class
     */
    public Class<?> getBeansClass(Class<?> clz) {
        return ComponentImpl.getClassByClass(clz);
    }

    /**
     * /**
     * 增加一个没有Component注解的class为 Component
     *
     * @param clz       class实例
     * @param scopeEnum class实例范围
     */
    public void addComponent(Class<?> clz, ScopeEnum scopeEnum) {
        ComponentImpl.addComponent(clz, scopeEnum);
    }


    /**
     * 检查class是否存在已创建好的bean
     *
     * @param aclass 要检测的class
     * @return 已存在bean为true 否则为false
     */
    public boolean isBean(Class<?> aclass) {
        return objectBeanStorage.containsKey(aclass);
    }

    /**
     * 创建一个bean的task任务
     * 为了打破循环依赖，这个任务由当前注入对象事件进行监听，等待所有资源初始化完成后进行统一注入
     *
     * @param aclass 要创建的class
     */
    public synchronized void newBeanTask(Class<?> aclass) {
        if (isBean(aclass) && ComponentImpl.isScopeSingleton(aclass)) {
            //第一阶段，检测一级缓存中是否已存在当前aclass
            //存在bean
        } else if (singletonFactories.containsKey(aclass)) {
            //第二阶段，检测二级缓存中是否已存在当前aclass
            // 存在bean 将当前bean返回
            Bean bean = singletonFactories.get(aclass);
            //将这个bean放入一级缓存
            addBean(bean);
            singletonFactories.remove(aclass);
        } else {
            //创建一个待初始化的bean放入二级缓存
            Bean bean = new Bean(ProxyFactory.invoke(aclass));
            singletonFactories.put(aclass, bean);
            //检测资源注入,并且加入事件
            autowired.analyseFields(bean.getObject(), true);
            ApplicationEventPublisher.publishEvent(new AutowiredEvent(Object.class));
        }
    }

    /**
     * 在所有bean创建完成之后将剩下没有循环依赖的bean放入存储器
     */
    private void addBeanSingletonFactories() {
        singletonFactories.values().forEach(this::addBean);
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
            Class aclass = bean.getObject().getClass().getSuperclass();
            //删除bean存储器
            if (!isBean(aclass)) return false;
            LinkedList<Bean> beans = objectBeanStorage.get(aclass);
            if (beans.isEmpty()) return false;
            int index = beans.size() - 1;
            if (index < 0) index = 0;
            beans.remove(index);
            if (beans.isEmpty()) objectBeanStorage.remove(aclass);
            else objectBeanStorage.put(aclass, beans);
            logger.debug("remove bean class:" + aclass + " index:" + bean.getIndex());
            return true;
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
    public LinkedList<Bean> getBeanList(Class<?> aclass) {
        //检测接口的实现
        Class<?> classt = this.getComponentClass(aclass);
        LinkedList<Bean> beans = new LinkedList<>();
        if (isBean(classt)) {
            beans = objectBeanStorage.get(classt);
        }
        return beans;
    }

    /**
     * 增加一个bean到存储器
     *
     * @param bean 要增加的bean
     * @return 返回增加后的bean，主要是增加了索引
     */
    public Bean addBean(Bean bean) {
        Class<?> aclass = bean.getObject().getClass().getSuperclass();
        synchronized (this) {
            LinkedList<Bean> beans = this.getBeanList(aclass);
            bean.setIndex(beans.size() + 1);
            beans.add(bean);
            objectBeanStorage.put(aclass, beans);
        }
        return bean;
    }

    /**
     * 获得一个资源就绪的bean(最先创建的bean)
     *
     * @param aclass 要获得的实例
     * @return 返回获得的bean
     */
    public Bean getBeanFirst(Class<?> aclass) {
        //检测接口的实现
        Class<?> classt = this.getComponentClass(aclass);
        Bean bean = null;
        if (isBean(classt)) {
            //存在bean 直接返回第一个bean
            bean = objectBeanStorage.get(classt).getFirst();
        }
        return bean;
    }

    /**
     * 获得一个资源就绪的bean(最先创建的bean)
     *
     * @param name 要获得的实例的别名
     * @return 返回获得的bean
     */
    public Bean getBeanFirst(String name) {
        return getBeanFirst(ComponentImpl.getClassByName(name));
    }

    /**
     * 获得一个资源就绪的bean(最后创建的bean)
     *
     * @param name 要获得的实例
     * @return 返回获得的bean
     */
    public Bean getBeanLast(String name) {
        return getBeanLast(ComponentImpl.getClassByName(name));
    }

    /**
     * 获得一个资源就绪的bean(最后创建的bean)
     *
     * @param aclass 要获得的实例
     * @return 返回获得的bean
     */
    public Bean getBeanLast(Class<?> aclass) {
        //检测接口的实现
        Class<?> classt = this.getComponentClass(aclass);
        Bean bean = null;
        if (isBean(classt)) {
            //存在bean 直接返回最后一个bean
            bean = objectBeanStorage.get(classt).getLast();
        }
        return bean;
    }

    /**
     * 获得一个资源就绪的bean(指定索引，默认是第一个)
     *
     * @param name  要获得的实例
     * @param index 要获得的实例的索引，下标从1开始
     * @return 返回获得的bean
     */
    public Bean getByIndexBean(String name, int index) {
        return getByIndexBean(ComponentImpl.getClassByName(name), index);
    }

    /**
     * 获得一个资源就绪的bean(指定索引，默认是第一个)
     *
     * @param aclass 要获得的实例
     * @param index  要获得的实例的索引，下标从1开始
     * @return 返回获得的bean
     */
    public Bean getByIndexBean(Class<?> aclass, int index) {
        if (index <= 1) index = 0;
        else index--;
        LinkedList<Bean> beans = this.getBeanList(aclass);
        if (beans.size() < index) CustomException.throwOut("index  Crossing the line  Beans size");
        return beans.get(index);
    }

    /**
     * 拷贝一个对象
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public void copyBean(Object source, Object target) {
        CachedBeanCopier.copy(source, target, source.getClass().getName().contains(CGLIB_TAG),
                target.getClass().getName().contains(CGLIB_TAG));
    }

    /**
     * 根据一个 aclass 创建一个全新的bean
     * 注意：会更加当前class的@Scope注解进行bean初始化，
     * 如果@Scope=SINGLETON且已存在，会直接返回当前已存在的bean实例
     * 只有当@Scope=PROTOTYPE时，才会忽略当前已存在的bean，创建一个全新的bean
     * 但是当前bean有被其他对象使用的可能性，为了避免此情况，需要增加
     *
     * @param aclass 目标对象
     * @return 返回全新的bean
     */
    public Bean createBean(Class<?> aclass) {
        //检测接口的实现
        Class<?> classt = this.getComponentClass(aclass);
        //未新创建之前的数量
        int count = getBeanList(classt).size();
        newBeanTask(classt);
        //已经有实例了并且是多实例
        if (getBeanList(classt).size() <= count && !ComponentImpl.isScopeSingleton(classt)) newBeanTask(classt);
        return getBeanLast(classt);
    }

    /**
     * 获得field上的对应资源实例
     * 对应的资源实例必须有@Component注解
     *
     * @param aclassz class实例
     * @return 返回对应的资源
     */
    public Class<?> getComponentClass(Class<?> aclassz) {
        //默认field是一个类
        Class<?> aclass = aclassz;
        //判断field是否是接口
        if (aclassz.isInterface()) {
            //是一个接口，寻找所有的实现类
            List<Class<?>> classImplList = Reflection.getInterfaceImplClass(aclassz, false);
            if (classImplList.isEmpty()) {
                logger.warn(aclassz.getName() + " classImplList count is 0");
                return null;
            }
            //过滤cglib代理
            List<Class<?>> classList = classImplList.stream().filter(cla -> !cla.getName().contains(BeanFactory.CGLIB_TAG)).collect(Collectors.toList());
            //如果发现多个实现，这里不能进行处理了
            if (classList != null && classList.size() >= 2) {
                logger.warn(aclassz.getName() + " classImplList is not the only");
                return null;
            }
            //找到唯一的实现类
            assert classList != null;
            aclass = classList.get(0);
        }
        //找到属于@Component的类
        aclass = getBeansClass(aclass);
        if (null == aclass) aclass = aclassz;
        if (!aclass.isAssignableFrom(aclassz)) logger.info(aclassz.getName() + " 替换为 " + aclass.getName());
        return aclass;
    }


    /**
     * 开始阻塞
     */
    public static void blockStart() {
        try {
            LOCK.lock();
            logger.info("beanFactory launch successful");
            STOP.await();
        } catch (InterruptedException e) {
            logger.warn(" service   stopped, interrupted by other thread!", e);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 结束阻塞
     * 默认延迟100ms
     *
     * @param millis 延迟关闭的毫秒数
     */
    public static void blockStop(long millis) {
        if (millis <= 0) millis = 100;
        try {
            LOCK.lock();
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                logger.error("延迟线程异常", e);
            }
            logger.info("beanFactory stop successful");
            STOP.signal();
        } finally {
            LOCK.unlock();
        }
        System.exit(0);
    }

}
