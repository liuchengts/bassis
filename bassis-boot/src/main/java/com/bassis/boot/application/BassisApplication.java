package com.bassis.boot.application;


import com.bassis.bean.BeanFactory;
import com.bassis.boot.common.Declaration;
import com.bassis.boot.common.ApplicationConfig;

import com.bassis.tools.exception.CustomException;
import org.apache.log4j.Logger;


/**
 * 启动起点
 */
public class BassisApplication {
    private static final long serialVersionUID = 1L;
    static final Logger logger = Logger.getLogger(BassisApplication.class);
    private static ApplicationConfig appApplicationConfig = new ApplicationConfig();
    private static TomcatUtil tomcatUtil = TomcatUtil.getInstance();
    private static MainArgsUtil mainArgsUtil = MainArgsUtil.getInstance();
    private static boolean startSchemaCoreFag = true;

    /**
     * 带参数启动
     * 优先以 com.lc.grpc.service.application.properties文件配置为准
     *
     * @param aClass 调起BassisApplication的类实例
     * @param args   参数（暂时忽略当前参数）
     */
    public static void run(Class aClass, String[] args) {
        appApplicationConfig = AutoConfig.readProperties(aClass, appApplicationConfig);
        mainArgsUtil.setArgs(args);
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
     * 启动框架
     */
    private static void start() {
        //启动 BeanFactory
        logger.debug("BeanFactory start...");
        BeanFactory.startBeanFactory(appApplicationConfig.getScanRoot());
        logger.debug("Application startSchema : " + appApplicationConfig.getStartSchema());
        switch (appApplicationConfig.getStartSchema()) {
            case Declaration.startSchemaWeb:
                tomcatUtil.start(appApplicationConfig);
                break;
            case Declaration.startSchemaCore:
                new Thread(() -> {
                    while (startSchemaCoreFag) {
                        try {
                            Thread.sleep(10000);
                        } catch (Exception e) {
                            CustomException.throwOut(" start [" + Declaration.startSchemaCore + "] error ", e);
                        }
                    }
                }).start();
                break;
            case Declaration.startSchemaRpc:
                //TODO rpc启动
                break;
            default:
                //web启动
                tomcatUtil.start(appApplicationConfig);
                //TODO rpc启动
                break;
        }
    }

    /**
     * 停止框架
     */
    private static void stop() {
        switch (appApplicationConfig.getStartSchema()) {
            case Declaration.startSchemaWeb:
                tomcatUtil.down();
                break;
            case Declaration.startSchemaCore:
                startSchemaCoreFag = false;
                break;
            case Declaration.startSchemaRpc:
                //TODO rpc关闭
                break;
            default:
                //tomcat关闭
                tomcatUtil.down();
                //TODO rpc关闭
                break;
        }
    }
}
