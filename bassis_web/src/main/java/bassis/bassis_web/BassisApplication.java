package bassis.bassis_web;

import bassis.bassis_bean.ReferenceDeclaration;
import bassis.bassis_web.filter.CharacterEncodingFilter;
import bassis.bassis_web.filter.RootFilter;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.juli.logging.Log;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.Filter;
import javax.servlet.Servlet;

public class BassisApplication {

    static String scanRoot;
    final Log log = LogFactory.getLog(getClass());
    Tomcat tomcat;
    Connector connector;
    Context context;
    BassisServlet bassisServlet;
    String bassisServletName;
    int port = 8080;

    private static class LazyHolder {
        private static final BassisApplication INSTANCE = new BassisApplication();
    }

    private BassisApplication() {
        tomcat = new Tomcat();
        bassisServlet = new BassisServlet();
        bassisServletName = BassisServlet.class.getName();
    }

    protected static final BassisApplication getInstance() {
        return BassisApplication.LazyHolder.INSTANCE;
    }

    public static void run(Class cla, String[] args) {
        scanRoot = cla.getPackage().getName();
        getInstance().start();
    }

    public static void run(Class cla, String[] args, int port) {
        getInstance().port = port;
        run(cla, args);
    }

    public static void stop() {
        getInstance().down();
    }

    protected void start() {
        connector = tomcat.getConnector();
        connector.setPort(port);
        connector.setURIEncoding("UTF-8");
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);
        //设置上下文
        context = tomcat.addContext("", null);
        //加入bassisServlet,设置启动顺序为1
        addServlet(bassisServlet, "/", "/", 1);
        //加入编码拦截器
        addFilter(new CharacterEncodingFilter(), "CharacterEncodingFilte", "/", "/*");
        //加入请求root拦截器
        addFilter(new RootFilter(), "RootFilter", "/", "/*");
        try {
            tomcat.start();
            log.info("Tomcat " + bassisServletName + " started success !");
            tomcat.getServer().await();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 增加一个Servlet
     *
     * @param objServlet    Servlet实例
     * @param servletName   Servlet名称
     * @param urlPattern    请求的url配置
     * @param loadOnStartup 启动顺序 默认为0
     */
    public void addServlet(Servlet objServlet, String servletName, String urlPattern, int loadOnStartup) {
        Wrapper wrapper = Tomcat.addServlet(context, servletName, objServlet);
        //启动顺序
        wrapper.setLoadOnStartup(loadOnStartup);
        wrapper.addInitParameter(ReferenceDeclaration.SCANROOT, scanRoot);
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
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        filterMap.addServletName(servletName);
        filterMap.addURLPattern(urlPattern);
        context.addFilterMap(filterMap);
    }

    protected void down() {
        try {
            tomcat.stop();
            log.info("Tomcat " + bassisServletName + " stoped !");
        } catch (LifecycleException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
