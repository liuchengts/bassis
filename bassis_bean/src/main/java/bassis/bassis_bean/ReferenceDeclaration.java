package bassis.bassis_bean;

import java.util.HashMap;
import java.util.Map;

import bassis.bassis_tools.string.StringUtils;

public class ReferenceDeclaration {

	private static class LazyHolder {
		private static final ReferenceDeclaration INSTANCE = new ReferenceDeclaration();
	}

	private ReferenceDeclaration() {
	}

	public static final ReferenceDeclaration getInstance() {
		return LazyHolder.INSTANCE;
	}

	// ===========静态参数声明==============
	/**
	 * web.xml参数
	 */
	public static final String CONTEXTCONFIGLOCATION = "contextConfigLocation";
	/**
	 * 默认web启动参数
	 */
	public static final String SERVLET_PROPERTIES = "/bassis/bassis_web/resources/servlet.properties";
	/**
	 * 默认bean启动参数
	 */
	public static final String BEAN_PROPERTIES = "/bassis/bassis_bean/resources/bean.properties";

	/**
	 * 项目根目录
	 */
	public static final String PROJECTROOT = "projectRoot";
	/**
	 * 控制器根目录
	 */
	public static final String CONTROLLERPACKAGE = "controllerPackage";
	/**
	 * 控制器捕捉后缀
	 */
	public static final String CONTROLLERSUFFIX = "controllerSuffix";
	/**
	 * 容器启动必须的参数声明
	 */
	public static final String INIT_PARAMETER = CONTROLLERPACKAGE + "," + CONTROLLERSUFFIX;
	/**
	 * 扫描器起点
	 */
	public static final String SCANROOT = "scanRoot";
	/**
	 * 视图前缀
	 */
	public static final String VIEWURLPREFIX = "viewUrlPrefix";
	/**
	 * 视图后缀
	 */
	public static final String VIEWURLSUFFIX = "viewUrlSuffix";

	/**
	 * bassis_hibernate包检查
	 */
	public static final String BASSIS_HIBERNATE = "bassis.bassis_hibernate.annotation.impl.DataImpl";
	/**
	 * bassis_hibernate dbIoc方法
	 */
	public static final String BASSIS_HIBERNATE_DBIOC = "dbIoc";
	/**
	 * 项目根目录
	 */
	private static String projectRoot;
	/**
	 * 控制器根目录
	 */
	private static String controllerPackage;
	/**
	 * 控制器捕捉后缀
	 */
	private static String controllerSuffix;
	/**
	 * 扫描器起点
	 */
	private static String scanRoot;
	/**
	 * 视图前缀
	 */
	private static String viewUrlPrefix;
	/**
	 * 视图后缀
	 */
	private static String viewUrlSuffix;

	/**
	 * 项目根目录
	 */
	public static String getProjectRoot() {
		return projectRoot;
	}

	/**
	 * 项目根目录
	 */
	public static void setProjectRoot(String projectRoot) {
		ReferenceDeclaration.projectRoot = projectRoot;
	}

	/**
	 * 控制器根目录
	 */
	public static String getControllerPackage() {
		return controllerPackage;
	}

	/**
	 * 控制器根目录
	 */
	public static void setControllerPackage(String controllerPackage) {
		ReferenceDeclaration.controllerPackage = controllerPackage;
	}

	/**
	 * 控制器捕捉后缀
	 */
	public static String getControllerSuffix() {
		return controllerSuffix;
	}

	/**
	 * 控制器捕捉后缀
	 */
	public static void setControllerSuffix(String controllerSuffix) {
		ReferenceDeclaration.controllerSuffix = controllerSuffix;
	}

	/**
	 * 扫描器起点
	 */
	public static String getScanRoot() {
		if (StringUtils.isEmptyString(scanRoot))
			scanRoot = projectRoot;

		return scanRoot;
	}

	/**
	 * 扫描器起点
	 */
	public static void setScanRoot(String scanRoot) {
		ReferenceDeclaration.scanRoot = scanRoot;
	}

	/**
	 * 视图前缀
	 */
	public static String getViewUrlPrefix() {
		if (StringUtils.isEmptyString(viewUrlPrefix))
			viewUrlPrefix = "/WEB-INF/";

		return viewUrlPrefix;
	}

	/**
	 * 视图前缀
	 */
	public static void setViewUrlPrefix(String viewUrlPrefix) {
		ReferenceDeclaration.viewUrlPrefix = viewUrlPrefix;
	}

	/**
	 * 视图后缀
	 */
	public static String getViewUrlSuffix() {
		return viewUrlSuffix;
	}

	/**
	 * 视图后缀
	 */
	public static void setViewUrlSuffix(String viewUrlSuffix) {
		ReferenceDeclaration.viewUrlSuffix = viewUrlSuffix;
	}

	private static Map<String, Integer> map = new HashMap<String, Integer>();

	/**
	 * 获得视图类型
	 * 
	 * @param key
	 * @return
	 */
	public static int getViewType(String key) {
		if (map.containsKey(key))
			return map.get(key);

		return 0;
	}

}
