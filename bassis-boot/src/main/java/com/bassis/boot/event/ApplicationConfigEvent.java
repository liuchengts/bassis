package com.bassis.boot.event;

import com.bassis.bean.event.ApplicationEvent;

import java.util.Map;

public class ApplicationConfigEvent extends ApplicationEvent {
    public final static int servlet = 1;
    public final static int filter = 2;
    /**
     * servlet名称
     */
    private String servletName;
    /**
     * 过滤器名称
     */
    private String filterName;
    /**
     * 参数
     */
    private Map<String, String> parameters;
    /**
     * 启动顺序
     */
    private int startUp;
    /**
     * 路径
     */
    private String urlPattern;
    /**
     * 类型
     */
    private int type;

    public static int getServlet() {
        return servlet;
    }

    public static int getFilter() {
        return filter;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public int getStartUp() {
        return startUp;
    }

    public void setStartUp(int startUp) {
        this.startUp = startUp;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ApplicationConfigEvent(Object source) {
        super(source);
    }

    public ApplicationConfigEvent(Object source, String servletName, int startUp, int type) {
        super(source);
        this.servletName = servletName;
        this.startUp = startUp;
        this.type = type;
    }

    public ApplicationConfigEvent(Object source, String servletName, String filterName, String urlPattern, int type) {
        super(source);
        this.servletName = servletName;
        this.filterName = filterName;
        this.urlPattern = urlPattern;
        this.type = type;
    }
}
