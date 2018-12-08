//package bassis.bassis_web;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//
//import bassis.bassis_bean.BeanFactory;
//import bassis.bassis_bean.IocFactory;
//import bassis.bassis_bean.ReferenceDeclaration;
//import bassis.bassis_bean.proxy.BeanInvoke;
//import CustomException;
//import GcUtils;
//import FileProperties;
//import StringUtils;
//import bassis.bassis_web.annotation.impl.ControllerImpl;
//import bassis.bassis_web.annotation.impl.InterceptorImpl;
//import bassis.bassis_web.assist.Resource;
//import bassis.bassis_web.assist.ServletAttribute;
//import bassis.bassis_web.assist.ServletClient;
//import bassis.bassis_web.assist.ServletCookie;
//import bassis.bassis_web.assist.ServletResource;
//import bassis.bassis_web.assist.ServletView;
//import bassis.bassis_web.assist.StewardResource;
//import bassis.bassis_web.ioc.Ioc;
//import bassis.bassis_web.work.Page;
//import bassis.bassis_web.work.ResultBean;
//import bassis.bassis_web.work.View;
//
///**
// * Servlet implementation class ContainerServlet
// */
//public class BassisContainer extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//	static ServletConfig servletConfig;
//	static ServletContext servletContext;
//	static FileProperties properties;
//	static final Logger logger = Logger.getLogger(BassisContainer.class);
//	static Map<String, Class<?>> mapActions = new HashMap<String, Class<?>>();
//	static Map<String, Method> mapMethods = new HashMap<String, Method>();
//
//	/**
//	 * @see Servlet#init(ServletConfig)
//	 */
//	public void init(ServletConfig config) throws ServletException {
//		// TODO Auto-generated method stub
//		servletConfig = config;
//		// 获得上下文
//		servletContext = config.getServletContext();
//		// 获得读取器
//		properties = BeanFactory.getProperties();
//		try {
//			webConfig();
//			launch();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			CustomException.throwOut(e.getMessage(), e);
//		}
//
//	}
//
//	/**
//	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void service(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		request.setCharacterEncoding("utf-8");
//		logger.debug("初始化service资源...");
//		ServletAttribute servletAttribute = ServletAttribute.init(servletContext, request, response);
//		ServletResource servletResource = ServletResource.init(servletAttribute);
//		ServletClient servletClient = ServletClient.init(servletAttribute);
//		ServletCookie servletCookie = ServletCookie.init(servletAttribute);
//		Class<?> las = null;
//		Method method = null;
//		Object obj = null;
//		Resource resource = null;
//		Object res = null;
//		try {
//			// 获得控制器
//			las = matchingAction(servletResource);
//			// 获得方法
//			method = matchingMethod(servletResource);
//			// 为请求资源创建Resource对象,此时未实例化控制器对象，当前资源未绑定到控制器
//			resource = createResource(servletAttribute, servletClient, servletCookie, servletResource);
//			// 这里进行前置拦截器处理,拦截器不用考虑控制器是否已经初始化
//			InterceptorImpl.init().interceptor(las, resource);
//			// 生产出控制器obj对象 相当于new 并且交给bean管理
//			obj = BeanFactory.getClassLoaderStorageToObject(las);
//			// 进行资源绑定
//			resource = bindResource(obj.toString(), resource);
//			//进行ioc注入
//			ioc(obj, servletResource);
//			try {
//				// 进行代理
//				res = BeanInvoke.invokeMethod(obj, method, resource);
//			} catch (Exception e) {
//				CustomException.throwOut("exe method failure", e);
//			}
//
//			// //这里需要写一个db管理池
//			// // 这里的ioc应该包含常规的ioc注入与dao注入
//			// Set<DaoImpl> setDao = new HashSet<DaoImpl>();
//			// boolean tx = false;
//			// try {
//			//
//			// // 进行data注入
//			// DataImpl db = new DataImpl();
//			// db.fieldIOC(las, setDao, method.getName());
//			// // 判断是否开启了事物
//			// tx = db.isTx();
//			// logger.debug("IOC处理完成 | 事务状态：" + tx);
//			// } catch (Exception e) {
//			// // TODO: handle exception
//			// CustomException.throwOut(e.getMessage(), e);
//			// }
//			// Object res = null;
//			// try {
//			// // 进行代理
//			// res = BeanInvoke.invokeMethod(las, method, resource);
//			// // 事物提交
//			// if (tx) {
//			// for (DaoImpl dao : setDao) {
//			// dao.commit();
//			// }
//			// }
//			// } catch (Exception e) {
//			// // TODO: handle exception
//			// // 进行事务回滚
//			// if (tx) {
//			// for (DaoImpl dao : setDao) {
//			// dao.rollBack();
//			// }
//			// }
//			// CustomException.throwOut("exe method failure", e);
//			// } finally {
//			// // 进行连接关闭？ 有可能c3p0自动进行了连接回收不需要这里处理了
//			// if (tx) {
//			// for (DaoImpl dao : setDao) {
//			// dao.close();
//			// }
//			// }
//			// }
//			// 初始化视图
//			initView(servletAttribute, servletResource, res);
//			// 处理结果
//			logger.debug("IOC处理完成");
//		} catch (Exception e) {
//			// TODO: handle exception
//			CustomException.throwOut("service init controller failure", e);
//		} finally {
//			clear(las, servletAttribute, servletResource, servletClient, servletCookie);
//			GcUtils.getInstance();
//		}
//		logger.debug("service方法完成");
//	}
//
//	/**
//	 * 资源清理
//	 */
//	private static void clear(Class<?> las, ServletAttribute servletAttribute, ServletResource servletResource,
//			ServletClient servletClient, ServletCookie servletCookie) {
//		// 清除掉这个管家对象
//		StewardResource.remove(servletResource.getControllerPath());
//		// 移除bean工厂创建的实例
//		BeanFactory.remove(las);
//		servletAttribute = null;
//		servletResource = null;
//		servletClient = null;
//		servletCookie = null;
//		logger.debug("资源清理完成");
//	}
//
//	/**
//	 * 配置返回视图
//	 *
//	 * @param servletAttribute
//	 * @param servletResource
//	 * @param res
//	 * @throws Exception
//	 */
//	private static void initView(ServletAttribute servletAttribute, ServletResource servletResource, Object res)
//			throws Exception {
//		ServletView servletView = ServletView.init(servletAttribute, servletResource);
//		int viewType = servletResource.getResourceType();
//		servletView.sendConfig(viewType);
//		// 判定res的类型//基于方法返回的数据
//		if (null == res) {
//			// 没有任何输出 直接系统输出访问成功
//			ResultBean r = new ResultBean(true, ServletView.SUCCESS, "操作成功", null);
//			servletView.outJson(r);
//			return;
//		} else if (res instanceof View) {
//			View v = (View) res;
//			servletView.setRlt(v.getRlt());
//			servletView.setSendType(v.isSendType());
//			servletView.setSendUrl(v.getSendUrl());
//		} else if (res instanceof String) {
//			String r = (String) res;
//			if (StringUtils.startsWith(r, "{") || ServletView.json == viewType) {
//				// json输出
//				servletView.outString(r);
//				return;
//			} else {
//				// 转发路径
//				servletView.setSendUrl(r);
//			}
//		} else if (res instanceof Page<?>) {// 分页构造参数
//			servletView.outJson_root(res);
//			return;
//		} else {
//			// 当做objjson出去
//			servletView.outJson(res);
//			return;
//		}
//		// 需要配置是转发还是重定向 需要配置转发页面 //false 表示转发 true表示重定向
//		if (StringUtils.isEmptyString(servletView.getSendUrl()) && viewType != ServletView.json) {
//			// 不是返回json数据且没有配置地址
//			// 跳转到404页面
//			servletView.setSendType(true);
//			servletView.setSendUrl(ServletView.error_404);
//		}
//		servletView.send();
//	}
//
//	/***
//	 * 在这里根据请求路径去匹配注解 获得对应的控制器类
//	 *
//	 * @param servletResource
//	 * @throws Exception
//	 */
//	private void analyzeRequest(ServletResource servletResource) throws Exception {
//		String path = StringUtils.startslastIndexOf_str(servletResource.getPath(), ".");
//		logger.debug("开始匹配控制器，筛选出可能的控制器：" + path);
//		Map<String, Class<?>> waitMap = new HashMap<String, Class<?>>();
//		String[] pathArr = path.substring(1).split("/");
//		String cm = "";
//		for (int i = 0; i < pathArr.length - 1; i++) {
//			cm = cm + "/" + pathArr[i];
//			Class<?> cl = ControllerImpl.getMapClass(cm);
//			if (null == cl) {
//				continue;
//			}
//			waitMap.put(cm, cl);
//			break;
//		}
//		if (null == waitMap || waitMap.isEmpty()) {
//			CustomException.throwOut("controller is  not ");
//			return;
//		}
//		Map<String, Map<String, Method>> waitMethodMap = new HashMap<String, Map<String, Method>>();
//		String actionPath = null;
//		Class<?> actionClass = null;
//		// 分析waitMap
//		for (String key : waitMap.keySet()) {
//			Map<String, Method> met = ControllerImpl.getMapMethod(key);
//			if (null == met) {
//				continue;
//			}
//			waitMethodMap.put(key, met);
//			// 现在确定的请求控制器路径为key 控制器实例为waitMap.get(key);
//			actionPath = key;
//			actionClass = waitMap.get(key);
//			servletResource.setControllerPath(actionPath);
//		}
//		if (null == actionClass || null == actionPath || null == waitMethodMap || waitMethodMap.isEmpty()) {
//			return;
//		}
//		// 分析waitMethodMap
//		// 得到方法路径
//		String methodPath = StringUtils.replace(path, actionPath, "");
//		servletResource.setMethodPath(methodPath);
//		Map<String, Method> mMap = waitMethodMap.get(actionPath);
//		if (!mMap.containsKey(methodPath))
//			return;
//
//		// 得到方法
//		Method method = mMap.get(methodPath);
//		mapActions.put(servletResource.getPath(), actionClass);
//		mapMethods.put(servletResource.getPath(), method);
//		logger.debug("请求资源 控制器：" + actionClass.getName() + " | 方法：" + method.getName());
//	}
//
//	/**
//	 * @see Servlet#destroy()
//	 */
//	public void destroy() {
//		// TODO Auto-generated method stub
//		servletConfig = null;
//		servletContext = null;
//		properties = null;
//		mapActions = null;
//		mapMethods = null;
//	}
//
//	/**
//	 * 初始化web.xml文件配置，配置基础参数
//	 */
//	private void webConfig() throws Exception {
//		logger.debug("读取并配置启动参数...");
//		// 获得默认properties配置
//		properties.read(ReferenceDeclaration.SERVLET_PROPERTIES);
//		// 获得web配置参数
//		String contextConfigLocation = servletConfig.getInitParameter(ReferenceDeclaration.CONTEXTCONFIGLOCATION);
//		if (StringUtils.isEmptyString(contextConfigLocation)) {
//			CustomException.throwOut("init servlet failure : parameter [ " + ReferenceDeclaration.CONTEXTCONFIGLOCATION
//					+ " ] not allowed");
//			return;
//		}
//		properties.read(contextConfigLocation);
//		// 校验必须参数
//		if (!FileProperties.verification(ReferenceDeclaration.INIT_PARAMETER.split(","))) {
//			CustomException.throwOut("init servlet failure : INIT_PARAMETER not allowed [ "
//					+ ReferenceDeclaration.INIT_PARAMETER + " ]");
//			return;
//		}
//		// 初始化核心参数
//		ReferenceDeclaration.setControllerPackage(properties.getProperty(ReferenceDeclaration.CONTROLLERPACKAGE));
//		ReferenceDeclaration.setControllerSuffix(properties.getProperty(ReferenceDeclaration.CONTROLLERSUFFIX));
//		// 项目名称（需要做个寻址过滤器，为自定义访问路径提供注册功能）
//		String contextPath = servletContext.getContextPath();
//		ReferenceDeclaration.setProjectRoot(contextPath.substring(1));
//	}
//
//	/***
//	 * 设置扫描路径，并且启动框架
//	 */
//	private void launch() throws Exception {
//		logger.debug("开始启动框架...");
//		// 扫描路径 默认为当前项目路径下 取当前顶级包名和次级包名作为扫描路径
//		String scanRoot = StringUtils.indexOf_str(this.getClass().getName(), ".", 2);
//		// 寻找配置的扫描起点
//		scanRoot = FileProperties.verification(ReferenceDeclaration.SCANROOT)
//				? properties.getProperty(ReferenceDeclaration.SCANROOT) : scanRoot;
//		// 当所有扫描路径都不可用时 选择控制器根目录进行扫描，这是非常危险的，可能会忽略资源注解
//		scanRoot = StringUtils.isEmptyString(scanRoot) ? ReferenceDeclaration.getControllerPackage() : scanRoot;
//		// 设置扫描路径
//		ReferenceDeclaration.setScanRoot(scanRoot);
//		// 启动bean工厂，启动扫描
//		BeanFactory.init();
//		// 启动Controller注解分析
//		ControllerImpl.getInstance();
//		logger.debug("框架初始化完成...");
//	}
//
//	/**
//	 * 匹配控制器
//	 *
//	 * @param servletResource
//	 *            请求资源
//	 * @return 返回匹配到的控制器类
//	 * @throws Exception
//	 */
//	private Class<?> matchingAction(ServletResource servletResource) throws Exception {
//		String path = servletResource.getPath();
//		logger.debug("请求资源：" + path);
//		Class<?> las = mapActions.get(path);
//		Method method = mapMethods.get(path);
//		if (null == las || null == method) {
//			analyzeRequest(servletResource);
//			las = mapActions.get(path);
//			method = mapMethods.get(path);
//		}
//		if (null == las || null == method) {
//			CustomException.throwOut("没有找到资源：" + path);
//		}
//		return las;
//	}
//
//	/**
//	 * 匹配方法
//	 *
//	 * @param servletResource
//	 *            请求资源
//	 * @return 返回匹配到的方法对象
//	 * @throws Exception
//	 */
//	private Method matchingMethod(ServletResource servletResource) throws Exception {
//		return mapMethods.get(servletResource.getPath());
//	}
//
//	/***
//	 * 创建Resource对象
//	 *
//	 * @param servletAttribute
//	 * @param servletClient
//	 * @param servletCookie
//	 * @param servletResource
//	 * @return 返回Resource对象
//	 * @throws Exception
//	 */
//	private Resource createResource(ServletAttribute servletAttribute, ServletClient servletClient,
//			ServletCookie servletCookie, ServletResource servletResource) throws Exception {
//		Resource resource = new Resource(servletAttribute, servletClient, servletCookie, servletResource);
//		return resource;
//	}
//
//	/***
//	 * 绑定内存地址到资源对象
//	 *
//	 * @param memoryAddress
//	 * @param resource
//	 * @return
//	 * @throws Exception
//	 */
//	private Resource bindResource(String memoryAddress, Resource resource) throws Exception {
//		// 当前访问路径+uuId,保证Resource对象在当前进程的唯一性，避免因为线程不安全导致Resource对象异常
//		StewardResource.put(memoryAddress, resource);
//		return resource;
//	}
//
//	/**
//	 * IOC的注入
//	 * @param obj
//	 * @param servletResource
//	 * @throws Exception
//	 */
//	private void ioc(Object obj ,ServletResource servletResource) throws Exception{
//		// 进行ioc请求参数资源注入
//		IocFactory.analyseFieldIOC(obj, servletResource.getMap());
//		// 进行ioc接口及resource资源注入
//		Ioc.iocResource(obj, servletResource.getMap());
//		// 判断是否需要进行ioc db资源注入
//		Ioc.isIocDB(obj);
//	}
//}
