package org.bassis.bean.web;

import org.bassis.tools.properties.FileProperties;

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

import org.apache.log4j.Logger;

/**
 * Servlet 核心容器
 */
public class BassisServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    static ServletConfig servletConfig;
    static ServletContext servletContext;
    static FileProperties properties;
    private static Logger logger = Logger.getLogger(BassisServlet.class);
    static Map<String, Class<?>> mapActions = new HashMap<>();
    static Map<String, Method> mapMethods = new HashMap<>();


    public void init(ServletConfig config) {
        logger.debug("初始化" + this.getClass().getName());
        servletConfig = config;
        // 获得上下文
        servletContext = config.getServletContext();
        //初始化配置文件读取器
        properties = FileProperties.getInstance();


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


}
