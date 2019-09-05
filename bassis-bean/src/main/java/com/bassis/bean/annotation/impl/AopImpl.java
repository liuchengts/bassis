package com.bassis.bean.annotation.impl;

import com.bassis.bean.annotation.Aop;
import com.bassis.bean.aop.AopService;
import com.bassis.bean.proxy.ProxyFactory;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.Reflection;
import com.bassis.tools.string.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

/**
 * aop切面实现,只针对方法级别
 *
 * @see Aop
 */
public class AopImpl {
    private static Logger logger = Logger.getLogger(AopImpl.class);

    public static final String PREHANDLE_NAME = "preHandle";// 前置
    public static final String POSTHANDLE_NAME = "postHandle";// 后置
    public static final String AFTERCOMPLETION_NAME = "afterCompletion";// 完成
    private Method preHandle = null; //前置方法
    private Method postHandle = null;//后置方法
    private Method afterCompletion = null;//完成方法
    private Object[] aopParameters; //aop方法携带的参数
    private Object object; //aop切点实例
    private Object aopObject; //aop实现类
    private Class aopService = AopService.class; //aop实现接口

    /**
     * 检测方法是否有aop注解
     *
     * @param method 要检测的方法
     * @return true表示有注解
     */
    public static boolean isAop(Method method) {
        return method.isAnnotationPresent(Aop.class);
    }

    /**
     * 实现 aop 注解
     *
     * @param object  aop切点方法所在的类
     * @param method  aop切点方法
     * @param objects aop切点方法的入参
     * @return 返回aop切点方法执行结果
     */
    public void analyseAop(Object object, Method method, Object[] objects) {
        String position = "bean:" + object.getClass().getName() + " method:" + method.getName();
        try {
            this.object = object;
            Aop aopAnnotation = method.getAnnotation(Aop.class);
            String value = aopAnnotation.value();
            Class aclass = aopAnnotation.aclass();
            List<Object> parameters = new ArrayList<>();
            String[] pAnnotation = aopAnnotation.parameters();
            if (!(pAnnotation.length == 1 && "".equals(pAnnotation[0]))) {
                parameters.addAll(Arrays.asList(pAnnotation));
            }
            if (objects.length >= 1) {
                //检测参数可用
                for (Object p : objects) {
                    if (null == p) {
                        parameters.add("null");
                    } else {
                        parameters.add(p);
                    }
                }
            }
            aopParameters = parameters.toArray();
            Set<Method> methods = null;
            if (null != aclass) {
                Map<Method, Boolean> mapMethods = ComponentImpl.getBeanAllMethods(aclass);
                if (!mapMethods.isEmpty()) {
                    methods = mapMethods.keySet();
                }
                if (null == methods) logger.warn(position + " @AopService aclass is null");
            } else if (!StringUtils.isEmptyString(value)) {
                aclass = ComponentImpl.getBeansClass(value);
                Map<Method, Boolean> mapMethods = ComponentImpl.getBeanAllMethods(value);
                if (!mapMethods.isEmpty()) {
                    methods = mapMethods.keySet();
                }
                if (null == methods) logger.warn(position + " @AopService value is null");
            } else {
                CustomException.throwOut(position + " @AopService error invalid parameter");
            }
            if (null == aclass) {
                CustomException.throwOut(position + " @AopService class is null , aop exit");
            }
            if (null == methods) {
                CustomException.throwOut(position + " @AopService methods is null , aop exit");
            }
            this.aopObject = ProxyFactory.invoke(aclass);
            methods.forEach(m -> match(Reflection.getMethod(true, aopService, m.getName(), m.getParameterTypes())));
            if (null == preHandle
                    || null == postHandle
                    || null == afterCompletion) {
                CustomException.throwOut(position + " Aop error invalid methods ");
            }
        } catch (Exception e) {
            CustomException.throwOut(position + " Aop exception", e);
        }
    }

    /**
     * 判断aop方法
     *
     * @param m 当前方法
     */
    private void match(Method m) {
        switch (m.getName()) {
            case PREHANDLE_NAME:
                // 前置
                preHandle = m;
                break;
            case POSTHANDLE_NAME:
                // 后置
                postHandle = m;
                break;
            case AFTERCOMPLETION_NAME:
                // 完成
                afterCompletion = m;
                break;
            default:
                break;
        }
    }

    /**
     * 前置方法
     *
     * @return 返回成功或失败
     * @throws Exception
     */
    public boolean preHandle() throws Exception {
        return (boolean) Reflection.invokeMethod(this.aopObject, preHandle, new Object[]{this.aopParameters});
    }

    /**
     * 后置方法
     *
     * @throws Exception
     */
    public void postHandle() throws Exception {
        Reflection.invokeMethod(this.aopObject, this.postHandle, new Object[]{this.aopParameters});
    }

    /**
     * 完成方法
     *
     * @throws Exception
     */
    public void afterCompletion() throws Exception {
        Reflection.invokeMethod(this.aopObject, this.afterCompletion, new Object[]{this.aopParameters});
    }
}
