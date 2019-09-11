package com.bassis.boot.web;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.annotation.Component;
import com.bassis.bean.common.Bean;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.boot.event.ServletEvent;
import com.bassis.boot.web.annotation.impl.ControllerImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.bassis.boot.web.assist.ServletAttribute;
import com.bassis.boot.web.assist.ServletClient;
import com.bassis.boot.web.assist.ServletCookie;
import com.bassis.boot.web.assist.ServletResource;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.Reflection;
import org.apache.log4j.Logger;

/**
 * Servlet 核心容器
 */
public class BassisServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(BassisServlet.class);
    static ServletConfig servletConfig;
    static ServletContext servletContext;
    final static BeanFactory beanFactory = BeanFactory.getInstance();

    //请求路径缓存
    static Map<String, Class<?>> mapActions = new ConcurrentHashMap<>();
    static Map<String, Method> mapMethods = new ConcurrentHashMap<>();

    public void init(ServletConfig config) {
        logger.debug("初始化 " + this.getClass().getName());
        servletConfig = config;
        // 获得上下文
        servletContext = config.getServletContext();
        //获得servlet启动参数
//        String scanRoot = servletConfig.getInitParameter(Declaration.scanRoot);
        //开始初始化Controller寻址
        ControllerImpl.getInstance();
        ApplicationEventPublisher.publishEvent(new ServletEvent(Object.class));
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("utf-8");
        logger.debug("初始化service资源...");
        try {
            ServletAttribute servletAttribute = ServletAttribute.init(servletContext, request, response);
            ServletResource servletResource = ServletResource.init(servletAttribute);
            ServletClient servletClient = ServletClient.init(servletAttribute);
            ServletCookie servletCookie = ServletCookie.init(servletAttribute);
            Class<?> actionCla = ControllerImpl.getMapClass(servletResource.getPath());
            Method method = ControllerImpl.getMapMethod(servletResource.getPath());
            if (null == actionCla || null == method) {
                CustomException.throwOut("没有找到资源:" + servletResource.getPath());
            }
            assert method != null;
            if (method.isVarArgs()) {
                CustomException.throwOut("可变参数:" + servletResource.getPath() + " method:" + method.getName());
            }
            List<Object> parameters = ControllerImpl.getMapParameter(method);
            //请求参数值，参数值类型
            Map<Object, Object> mapParameters = new LinkedHashMap<>();
            if (parameters != null) {
                //验证方法参数
                int count = parameters.size() / 3;
                if (count <= 0) count = 1;
                for (int i = 0; i < count; i = i + 3) {
                    String name = (String) parameters.get(i);
                    Class<?> type = (Class<?>) parameters.get(i + 1);
                    Boolean required = (Boolean) parameters.get(i + 2);
                    //检查必须参数
                    if (!servletResource.getParameters().containsKey(name) && required)
                        CustomException.throwOut("method required parameter : " + name + " is null [" + servletResource.getPath() + "]");
                    String[] ps = (String[]) servletResource.getParameters().get(name);
                    mapParameters.put(ps[0], type);
                }
            }
            //交由bean进行生产
            Bean bean = beanFactory.createBean(actionCla);
            logger.info("bean:" + bean.getObject().toString());
            Object resInvoke = Reflection.invokeMethod(bean.getObject(), method, mapParameters.keySet().toArray());
            logger.info("resInvoke : " + resInvoke);
            //清除资源
            beanFactory.removeBean(bean);
        } catch (Exception e) {
            CustomException.throwOut("controller error", e);
        }
        logger.debug("service方法完成");
    }

    public void destroy() {
        logger.debug("资源销毁");
    }


}
