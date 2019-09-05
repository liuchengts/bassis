package com.bassis.boot.container;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.boot.event.ServletEvent;
import com.bassis.boot.web.annotation.impl.ControllerImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        logger.debug("初始化service资源...");
        System.out.println(request.getContextPath());
//        ControllerImpl.getMapClass()


        logger.debug("service方法完成");
    }


    public void destroy() {
        logger.debug("资源销毁");
    }


}
