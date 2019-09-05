package com.bassis.boot.application;

import com.bassis.boot.common.ApplicationConfig;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.Map;

public class TomcatUtil {
    private static class LazyHolder {
        private static final TomcatUtil INSTANCE = new TomcatUtil();
    }

    private TomcatUtil() {
    }

    public static final TomcatUtil getInstance() {
        return TomcatUtil.LazyHolder.INSTANCE;
    }

    private Context context;
    private ApplicationConfig appApplicationConfig;

    public void init(Context context, ApplicationConfig appApplicationConfig) {
        this.context = context;
        this.appApplicationConfig = appApplicationConfig;
    }

    public void addServlet(Servlet objServlet, String servletName, int loadOnStartup) {
        addServlet(objServlet, servletName, appApplicationConfig.getUrlPattern(), loadOnStartup, null);
    }

    /**
     * 增加一个Servlet
     *
     * @param objServlet    Servlet实例
     * @param servletName   Servlet名称
     * @param urlPattern    请求的url配置
     * @param loadOnStartup 启动顺序 默认为0
     */
    public void addServlet(Servlet objServlet, String servletName, String urlPattern, int loadOnStartup, Map<String, String> initParameters) {
        Wrapper wrapper = Tomcat.addServlet(context, servletName, objServlet);
        //启动顺序
        wrapper.setLoadOnStartup(loadOnStartup);
        initParameters.forEach(wrapper::addInitParameter);
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
    public void addFilter(Filter objFilter, String filterName, String servletName, String urlPattern) {
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


}
