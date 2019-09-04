package com.bassis.web.core;

import com.bassis.bean.BeanFactory;
import com.bassis.bean.event.ApplicationEventPublisher;
import com.bassis.bean.event.ApplicationListener;
import com.bassis.bean.event.domain.ResourcesEvent;
import com.bassis.boot.application.TomcatUtil;
import com.bassis.boot.common.Declaration;
import com.bassis.boot.event.ApplicationConfigEvent;
import com.bassis.tools.properties.FileProperties;
import org.apache.catalina.core.ApplicationContext;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet web容器
 */
public class WebServlet extends HttpServlet implements ApplicationListener<ResourcesEvent> {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(WebServlet.class);
    static ServletConfig servletConfig;
    static ServletContext servletContext;
    final static FileProperties properties = FileProperties.getInstance();
    final static BeanFactory beanFactory = BeanFactory.getInstance();
    static Map<String, Class<?>> mapActions = new HashMap<>();
    static Map<String, Method> mapMethods = new HashMap<>();


    public void init(ServletConfig config) {
        logger.debug("初始化" + this.getClass().getName());
        servletConfig = config;
        // 获得上下文
        servletContext = config.getServletContext();
        //获得servlet启动参数
        String scanRoot = servletConfig.getInitParameter(Declaration.scanRoot);
        //开始初始化Controller寻址

    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        logger.debug("初始化service资源...");

        logger.debug("service方法完成");
    }


    public void destroy() {
        logger.debug("资源销毁");
    }


    @Override
    public void onApplicationEvent(ResourcesEvent event) {
        ApplicationEventPublisher.publishEvent(new ApplicationConfigEvent(this, this.getServletName(), 2, ApplicationConfigEvent.servlet));
    }
}
