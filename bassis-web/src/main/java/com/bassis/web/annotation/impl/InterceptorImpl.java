//package com.bassis.web.annotation.impl;
//
//import java.lang.reflect.Method;
//
//import org.apache.log4j.Logger;
//
//import com.bassis.tools.exception.CustomException;
//import com.bassis.web.annotation.Interceptor;
//
///**
// * 拦截器针对一个类进行拦截 aop是针对一个方法进行拦截
// *
// */
//public class InterceptorImpl {
//	private static Logger logger = Logger.getLogger(InterceptorImpl.class);
//
//
//	public static final String PREHANDLE_NAME = "preHandle";// 前置
//	public static final String POSTHANDLE_NAME = "postHandle";// 完成
//	public static final String AFTERCOMPLETION_NAME = "afterCompletion";// 异常
//	public static final int PREHANDLE_INDEX = 0;// 前置
//	public static final int POSTHANDLE_INDEX = 1;// 完成
//	public static final int AFTERCOMPLETION_INDEX = 2;// 异常
//	private static volatile boolean perform = true;// 正常执行
//	private Resource resource;
//	public static boolean isPerform() {
//		return perform;
//	}
//
//	private static void setPerform(boolean perform) {
//		InterceptorImpl.perform = perform;
//	}
//	/**
//	 * Interceptor执行
//	 *
//	 * @param las
//	 * @param resource
//	 * @throws Exception
//	 */
//	public void interceptor(Class<?> clz, Resource _resource) throws Exception {
//		if (!clz.isAnnotationPresent(Interceptor.class))
//			return;
//
//		Interceptor annotation = clz.getAnnotation(Interceptor.class);
//		resource=_resource;
//		// 输出注解上的属性
//		String path = annotation.value();
//		String arr[] = path.split(",");
//		for (String s : arr) {
//			Class<?> las = BeanFactory.getClassLoader(s);
//			Object obj = BeanFactory.getLoader(las);
//			impl(obj);
//		}
//	}
//
//	/**
//	 * 初始化Interceptor功能，建议每次调用Interceptor之前都执行此方法
//	 */
//	@SuppressWarnings("static-access")
//	public static InterceptorImpl init() {
//		InterceptorImpl impl=new InterceptorImpl();
//		impl.setPerform(true);
//		return impl;
//	}
//
//	/**
//	 * 代理执行
//	 *
//	 * @param method
//	 * @param phase
//	 * @throws Exception
//	 */
//	private void impl(Object obj) throws Exception {
//		pollingImpl(obj, PREHANDLE_INDEX);
//		pollingImpl(obj, POSTHANDLE_INDEX);
//	}
//
//	private  void pollingImpl(Object obj, int phase) {
//		Class<?> clz = obj.getClass();
//		Method[] methods = clz.getDeclaredMethods();
//		// 方法筛选
//		for (Method m : methods) {
//			switch (phase) {
//			case 1:
//				// 完成
//				if (m.getName().equals(POSTHANDLE_NAME) && perform) {
//					try {
//						m.invoke(obj, resource);
//					} catch (Exception e) {
//						// TODO: handle exception
//						CustomException.throwOut("Interceptor " + clz.getName() + "|" + InterceptorImpl.PREHANDLE_NAME,
//								e);
//					}
//				}
//				break;
//			case 2:
//				// 异常
//				if (m.getName().equals(AFTERCOMPLETION_NAME)) {
//					invokeMethod_afterCompletion(obj, methods);
//				}
//				break;
//			default:
//				// 前置
//				if (m.getName().equals(PREHANDLE_NAME) && perform)
//					invokeMethod_preHandle(obj, methods, m);
//				break;
//			}
//		}
//	}
//
//	/**
//	 * 前置
//	 *
//	 * @param las
//	 * @param methods
//	 * @param method
//	 * @return
//	 */
//	private Object invokeMethod_preHandle(Object obj, Method[] methods, Method method) {
//		Object res = null;
//		try {
//			res = method.invoke(obj,resource);
//			perform = (Boolean) res;
//			logger.debug("方法代理执行完成");
//		} catch (Exception e) {
//			// TODO: handle exception
//			invokeMethod_afterCompletion(obj, methods);
//			// 这里需要定义一个异常处理类
//			res = CustomException.parsing(e.getMessage(), e, res);
//		}
//		if (!perform) {
//			invokeMethod_afterCompletion(obj, methods);
//		}
//		return res;
//	}
//
//	/**
//	 * 异常
//	 *
//	 * @param obj
//	 * @param methods
//	 */
//	private void invokeMethod_afterCompletion(Object obj, Method[] methods) {
//		Class<?> las = obj.getClass();
//		try {
//			// 异常调用
//			for (Method m : methods) {
//				if (m.getName().equals(AFTERCOMPLETION_NAME)) {
//					m.invoke(obj,resource);
//				}
//			}
//		} catch (Exception e2) {
//			// TODO: handle exception
//			CustomException.throwOut("Interceptor " + las.getName() + "|" + InterceptorImpl.PREHANDLE_NAME, e2);
//		}
//	}
//}
