package com.bassis.boot.common;

import java.io.Serializable;

/**
 * 核心配置
 */
public class ApplicationConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    //######必须参数
    /**
     * 传入的启动类 必须传入
     */
    Class aClass;

    //######默认参数
    /**
     * 启动端口 默认
     */
    Integer port = 8080;
    /**
     * 上下文前缀 默认
     */
    String contextPath = "";
    /**
     * 请求路径后缀 默认
     */
    String urlPattern = "";
    /**
     * 请求系统服务路径后缀 默认
     */
    String urlSysPattern = "/system";
    /**
     * 框架启动模式 默认
     */
    String startSchema = "web";

    //######自动配置参数
    //自动生成
    /**
     * 扫描起点
     */
    String scanRoot;
    /**
     * servlet名称
     */
    String servletName = "/";


    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getScanRoot() {
        return scanRoot;
    }

    public void setScanRoot(String scanRoot) {
        this.scanRoot = scanRoot;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getUrlSysPattern() {
        return urlSysPattern;
    }

    public void setUrlSysPattern(String urlSysPattern) {
        this.urlSysPattern = urlSysPattern;
    }

    public String getStartSchema() {
        return startSchema;
    }

    public void setStartSchema(String startSchema) {
        this.startSchema = startSchema;
    }

    public void rootClass(Class aClass) {
        this.scanRoot = aClass.getPackage().getName();
        this.aClass = aClass;
        this.servletName = aClass.getSimpleName();
    }


}
