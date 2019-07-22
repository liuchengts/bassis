package org.bassis.boot.application;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.bassis.boot.common.ApplicationConfig;
import org.bassis.boot.common.Declaration;
import org.bassis.boot.container.BassisServlet;
import org.bassis.boot.web.filter.CharacterEncodingFilter;
import org.bassis.boot.web.filter.RootFilter;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动起点
 */
public class BassisApplication {
    private static final long serialVersionUID = 1L;
    static final Logger logger = Logger.getLogger(BassisApplication.class);
    static Tomcat tomcat;
    static Connector connector;
    static Context context;
    static BassisServlet bassisServlet;
    static ApplicationConfig appApplicationConfig;

    static {
        tomcat = new Tomcat();
        bassisServlet = new BassisServlet();
        appApplicationConfig = new ApplicationConfig();
    }

    private static void autoConfig(Class cla) {
        //调用自动配置
        appApplicationConfig.rootClass(cla, BassisApplication.class);
    }

    public static void run(Class cla, String[] args) {
        autoConfig(cla);
        start();
    }

    public static void run(Class cla, String[] args, int port) {
        appApplicationConfig.setPort(port);
        run(cla, args);
    }

    public static void run(String[] args, ApplicationConfig config) {
        appApplicationConfig = config;
        start();
    }

    public static void stop() {
        down();
    }

    /**
     * 启动
     */
    protected static void start() {
        connector = tomcat.getConnector();
        connector.setPort(appApplicationConfig.getPort());
        connector.setURIEncoding(Declaration.encoding);
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);
        //设置上下文
        context = tomcat.addContext(appApplicationConfig.getContextPath(), null);
        //加入bassisServlet,设置启动顺序为1
        addServlet(bassisServlet, appApplicationConfig.getServletName(), appApplicationConfig.getUrlPattern(), 1, initBassisServletParameters());
        //加入编码拦截器
        addFilter(new CharacterEncodingFilter(), "CharacterEncodingFilte", "/", "/*");
        //加入请求root拦截器
        addFilter(new RootFilter(), "RootFilter", "/", "/*");
        try {
            tomcat.start();
            logger.info("Tomcat " + appApplicationConfig.getServletName() + " started success !");
            tomcat.getServer().await();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 启动 bassisServlet 需要的初始化参数
     *
     * @return 返回参数结果
     */
    private static Map<String, String> initBassisServletParameters() {
        Map<String, String> initParameters = new HashMap<>();
        //加入启动扫描根节点
        initParameters.put(Declaration.scanRoot, appApplicationConfig.getScanRoot());
        return initParameters;
    }

    /**
     * 增加一个Servlet
     *
     * @param objServlet    Servlet实例
     * @param servletName   Servlet名称
     * @param urlPattern    请求的url配置
     * @param loadOnStartup 启动顺序 默认为0
     */
    public static void addServlet(Servlet objServlet, String servletName, String urlPattern, int loadOnStartup, Map<String, String> initParameters) {
        Wrapper wrapper = Tomcat.addServlet(context, servletName, objServlet);
        //启动顺序
        wrapper.setLoadOnStartup(loadOnStartup);
        initParameters.entrySet().forEach(m -> wrapper.addInitParameter(m.getKey(), m.getValue()));
        context.addServletMappingDecoded(urlPattern, servletName);
    }

    /**
     * 增加过滤器
     *
     * @param objFilter   过滤器实例
     * @param filterName  过滤器名称
     * @param servletName 绑定到servletName
     * @param urlPattern  过滤触发的url配置
     */
    public static void addFilter(Filter objFilter, String filterName, String servletName, String urlPattern) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(objFilter);
        filterDef.setFilterName(filterName);
        context.addFilterDef(filterDef);
        //绑定到现有 servlet上
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        filterMap.addServletName(servletName);
        filterMap.addURLPattern(urlPattern);
        context.addFilterMap(filterMap);
    }

    /**
     * 停止
     */
    protected static void down() {
        try {
            tomcat.stop();
            logger.info("Tomcat " + appApplicationConfig.getServletName() + " stoped !");
        } catch (LifecycleException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
