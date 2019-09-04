package com.bassis.boot.application;


import com.bassis.bean.BeanFactory;
import com.bassis.boot.web.filter.CharacterEncodingFilter;
import com.bassis.boot.web.filter.RootFilter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.common.Declaration;
import com.bassis.boot.container.BassisServlet;

import org.apache.log4j.Logger;

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
    static TomcatUtil tomcatUtil;

    static {
        tomcat = new Tomcat();
        bassisServlet = new BassisServlet();
        appApplicationConfig = new ApplicationConfig();
        tomcatUtil = TomcatUtil.getInstance();
    }

    /**
     * 带参数启动
     * 优先以 com.lc.grpc.service.application.properties文件配置为准
     *
     * @param aClass 调起BassisApplication的类实例
     * @param args   参数（暂时忽略当前参数）
     */
    public static void run(Class aClass, String[] args) {
        appApplicationConfig.rootClass(aClass);
        appApplicationConfig = AutoConfig.readProperties(appApplicationConfig);
        start();
    }

    /**
     * 带参数和端口启动
     * 优先以 com.lc.grpc.service.application.properties文件配置为准
     *
     * @param aClass 调起BassisApplication的类实例
     * @param args   参数（暂时忽略当前参数）
     * @param port   tomcat启动端口
     */
    public static void run(Class aClass, String[] args, int port) {
        appApplicationConfig.setPort(port);
        run(aClass, args);
    }

    /**
     * 带参数和容器配置启动
     * 会忽略当前 com.lc.grpc.service.application.properties 所有配置
     *
     * @param args   参数（暂时忽略当前参数）
     * @param config 配置
     */
    public static void run(String[] args, ApplicationConfig config) {
        appApplicationConfig = config;
        start();
    }

    /**
     * 停止tomcat
     */
    public static void stop() {
        down();
    }

    /**
     * 启动 tomcat
     */
    private static void start() {
        //启动 BeanFactory
        logger.debug("BeanFactory start...");
        BeanFactory.startBeanFactory(appApplicationConfig.getScanRoot());
        defaultConfig();
        //启动 tomcat
        logger.debug("Tomcat start...");
        connector = tomcat.getConnector();
        connector.setPort(appApplicationConfig.getPort());
        connector.setURIEncoding(Declaration.encoding);
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);
        try {
            tomcat.start();
            logger.info("Tomcat :[" + appApplicationConfig.getServletName() + "] port:[" + appApplicationConfig.getPort() + "] started success");
            logger.info("Tomcat contextPath:[" + appApplicationConfig.getContextPath() + "]");
            tomcat.getServer().await();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 默认配置
     */
    private static void defaultConfig() {
        //设置上下文
        context = tomcat.addContext(appApplicationConfig.getContextPath(), null);
        //初始化工具类
        tomcatUtil.init(context,appApplicationConfig);
        //加入bassisServlet,设置启动顺序为1
        tomcatUtil.addServlet(bassisServlet, appApplicationConfig.getServletName(), appApplicationConfig.getUrlSysPattern(), 1, initBassisServletParameters());
        //加入编码拦截器
        tomcatUtil.addFilter(new CharacterEncodingFilter(), CharacterEncodingFilter.class.getSimpleName(), "/", "/*");
        //加入请求root拦截器
        tomcatUtil.addFilter(new RootFilter(), RootFilter.class.getSimpleName(), "/", "/*");
        //发出默认配置完成通知
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
     * 停止
     */
    private static void down() {
        try {
            logger.debug("Tomcat " + appApplicationConfig.getServletName() + " stoped !");
            tomcat.stop();
        } catch (LifecycleException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }


}
