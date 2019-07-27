package com.bassis.bean.aop;

/**
 * aop 定义
 */
public interface AopService {
    /**
     * 前置方法
     */
    boolean preHandle(Object... objs);

    /**
     * 后置方法
     */
    void postHandle(Object... objs);

    /**
     * 完成方法
     */
    void afterCompletion(Object... objs);
}
