package bassis.bassis_web;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.juli.logging.Log;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.LogFactory;

public class BassisApplication {

    final Log log = LogFactory.getLog(getClass());
    Tomcat tomcat;
    Connector connector;
    Context context;
    BassisContainer bassisContainer;
    String bassisContainerName;
    int port = 8080;

    private static class LazyHolder {
        private static final BassisApplication INSTANCE = new BassisApplication();
    }

    private BassisApplication() {
        tomcat = new Tomcat();
        bassisContainer = new BassisContainer();
        bassisContainerName = BassisContainer.class.getName();
    }

    protected static final BassisApplication getInstance() {
        return BassisApplication.LazyHolder.INSTANCE;
    }

    public static void run() {
        getInstance().start();
    }

    public static void run(int port) {
        getInstance().port = port;
        getInstance().start();
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
        //启动容器框架
        context = tomcat.addContext("", null);
        Tomcat.addServlet(context, "/", bassisContainer);
        context.addServletMappingDecoded("/", "/");
        try {
            tomcat.init();
            tomcat.start();
            log.info("Tomcat " + bassisContainerName + " started success !");
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    protected void down() {
        try {
            tomcat.stop();
            log.info("Tomcat " + bassisContainerName + " stoped !");
        } catch (LifecycleException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
