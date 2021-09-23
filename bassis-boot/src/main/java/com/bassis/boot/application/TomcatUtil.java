package com.bassis.boot.application;

import com.bassis.boot.common.ApplicationConfig;
import com.bassis.boot.common.Declaration;
import com.bassis.boot.common.HttpPage;
import com.bassis.boot.common.MainArgs;
import com.bassis.boot.web.BassisServlet;
import com.bassis.boot.web.filter.CharacterEncodingFilter;
import com.bassis.boot.web.filter.RootFilter;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.ReflexUtils;
import com.bassis.tools.string.StringUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TomcatUtil {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(TomcatUtil.class);

    private static class LazyHolder {
        private static final TomcatUtil INSTANCE = new TomcatUtil();
    }

    private TomcatUtil() {
    }

    public static TomcatUtil getInstance() {
        return TomcatUtil.LazyHolder.INSTANCE;
    }

    private static MainArgs mainArgs = MainArgs.getInstance();
    private StandardContext context;
    private ApplicationConfig appApplicationConfig;
    private Tomcat tomcat = new Tomcat();
    private Connector connector;
    private BassisServlet bassisServlet = new BassisServlet();

    /**
     * 启动 tomcat
     *
     * @param appApplicationConfig 启动配置
     */
    protected void start(ApplicationConfig appApplicationConfig) {
        this.appApplicationConfig = appApplicationConfig;
        //设置tomcat
        tomcat.setBaseDir(System.getProperty("user.dir"));
        tomcat.setPort(appApplicationConfig.getPort());
        tomcat.getHost().setAutoDeploy(false);
        //设置编码
        connector = tomcat.getConnector();
        connector.setURIEncoding(Declaration.encoding);
        tomcat.setConnector(connector);
        //设置上下文
        context = new StandardContext();
        context.setPath(appApplicationConfig.getContextPath());
        context.addLifecycleListener(new Tomcat.FixContextListener());
        tomcat.getHost().addChild(context);
        //设置默认欢迎页
//        defaultIndexPage(null);
        //设置错误页面
//        defaultErrorPage();
        //设置tomcat服务
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);
        try {
            defaultConfig(mainArgs.getArgs());
            //启动 tomcat
            logger.info("Tomcat start...");
            tomcat.start();
            logger.info("Tomcat :[" + appApplicationConfig.getServletName() + "] port:[" + appApplicationConfig.getPort() + "] started success");
            logger.info("Tomcat contextPath:[" + appApplicationConfig.getContextPath() + "]");
            server.await();
        } catch (Exception e) {
            CustomException.throwOut(" start [" + Declaration.startSchemaWeb + "] error ", e);
        }
    }

    /**
     * 默认配置
     */
    private void defaultConfig(String[] args) {
        //设置上下文
        Map<String, String> servletParameters = initBassisServletParameters();
        if (args != null) servletParameters.put(Declaration.mainArgs, Arrays.toString(args));
        //加入bassisServlet,设置启动顺序为1
        addServlet(bassisServlet, appApplicationConfig.getServletName(), 1, servletParameters);
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
        addServlet(objServlet, servletName, loadOnStartup, null);
    }

    /**
     * 增加一个Servlet
     *
     * @param objServlet    Servlet实例
     * @param servletName   Servlet名称
     * @param loadOnStartup 启动顺序 默认为0
     */
    public void addServlet(Servlet objServlet, String servletName, int loadOnStartup, Map<String, String> initParameters) {
        Wrapper wrapper = tomcat.addServlet(context.getPath(), servletName, objServlet);
        //启动顺序
        wrapper.setLoadOnStartup(loadOnStartup);
        initParameters.forEach(wrapper::addInitParameter);
        context.addServletMappingDecoded("/", servletName);
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

    /**
     * 添加错误页面
     *
     * @param errorCode 错误码
     * @param location  本地页面地址
     */
    public void addErrorPage(int errorCode, String location) {
        ErrorPage errorPage = new ErrorPage();
        errorPage.setErrorCode(errorCode);
        errorPage.setLocation(location);
        context.addErrorPage(errorPage);
    }

    /**
     * 配置默认的错误页面
     */
    void defaultErrorPage() {
        addErrorPage(HttpServletResponse.SC_NOT_FOUND, HttpPage.ERROR_404);
        addErrorPage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, HttpPage.ERROR_500);
        addErrorPage(HttpServletResponse.SC_SERVICE_UNAVAILABLE, HttpPage.ERROR_503);
    }

    /**
     * 配置默认的欢迎页面
     */
    void defaultIndexPage(String location) {
        context.addWelcomeFile(StringUtils.isEmptyString(location) ? HttpPage.INDEX : location);
    }
}
