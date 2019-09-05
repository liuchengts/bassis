package com.bassis.boot.application;

import com.bassis.bean.BeanFactory;
import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.common.Declaration;
import com.bassis.boot.container.BassisServlet;
import com.bassis.boot.web.filter.CharacterEncodingFilter;
import com.bassis.boot.web.filter.RootFilter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

public class TomcatUtil {
    private static final long serialVersionUID = 1L;
    static final Logger logger = Logger.getLogger(TomcatUtil.class);

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
    private Tomcat tomcat = new Tomcat();
    private Connector connector;
    private BassisServlet bassisServlet = new BassisServlet();

    /**
     * 加入初始化 tomcat 参数
     *
     * @param appApplicationConfig 参数配置
     */
    protected void init(ApplicationConfig appApplicationConfig) {
        this.appApplicationConfig = appApplicationConfig;
    }

    /**
     * 启动 tomcat
     */
    protected void start() {
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
    private void defaultConfig() {
        //设置上下文
        context = tomcat.addContext(appApplicationConfig.getContextPath(), null);
        //加入bassisServlet,设置启动顺序为1
        addServlet(bassisServlet, appApplicationConfig.getServletName(), appApplicationConfig.getUrlSysPattern(), 1, initBassisServletParameters());
        //加入编码拦截器
        addFilter(new CharacterEncodingFilter(), CharacterEncodingFilter.class.getSimpleName(), "/", "/*");
        //加入请求root拦截器
        addFilter(new RootFilter(), RootFilter.class.getSimpleName(), "/", "/*");
        //发出默认配置完成通知
    }

    /**
     * 启动 bassisServlet 需要的初始化参数
     *
     * @return 返回参数结果
     */
    private Map<String, String> initBassisServletParameters() {
        Map<String, String> initParameters = new HashMap<>();
        //加入启动扫描根节点
        initParameters.put(Declaration.scanRoot, appApplicationConfig.getScanRoot());
        return initParameters;
    }


    /**
     * 停止 tomcat
     */
    protected void down() {
        try {
            logger.debug("Tomcat " + appApplicationConfig.getServletName() + " stoped !");
            tomcat.stop();
        } catch (LifecycleException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * 增加一个Servlet
     *
     * @param objServlet    Servlet实例
     * @param servletName   Servlet名称
     * @param loadOnStartup 启动顺序 默认为null
     */
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
