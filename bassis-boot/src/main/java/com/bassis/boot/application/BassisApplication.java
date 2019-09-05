package com.bassis.boot.application;


import com.bassis.boot.common.Declaration;
import com.bassis.boot.event.ApplicationConfigEvent;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.container.BassisServlet;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.Servlet;


/**
 * 启动起点
 */
public class BassisApplication {
    private static final long serialVersionUID = 1L;
    static final Logger logger = Logger.getLogger(BassisApplication.class);
    private static ApplicationConfig appApplicationConfig = new ApplicationConfig();
    private static TomcatUtil tomcatUtil = TomcatUtil.getInstance();


    /**
     * 带参数启动
     * 优先以 com.lc.grpc.service.application.properties文件配置为准
     *
     * @param aClass 调起BassisApplication的类实例
     * @param args   参数（暂时忽略当前参数）
     */
    public static void run(Class aClass, String[] args) {
        appApplicationConfig = AutoConfig.readProperties(aClass, appApplicationConfig);
        start(args);
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
     * 启动框架
     */
    private static void start(String[] args) {
        switch (appApplicationConfig.getStartSchema()) {
            case Declaration.startSchemaAll:
                break;
            case Declaration.startSchemaWeb:
                break;
            case Declaration.startSchemaCore:
                break;
            case Declaration.startSchemaRpc:
                break;

            default:
                //web启动
                break;
        }

        if (args == null) {

        }
    }

    /**
     * 停止框架
     */
    private static void stop() {

    }


}
