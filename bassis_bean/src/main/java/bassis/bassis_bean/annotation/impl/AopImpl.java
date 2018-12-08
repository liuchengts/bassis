package bassis.bassis_bean.annotation.impl;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import bassis.bassis_bean.BeanFactory;
import bassis.bassis_bean.annotation.Aop;
import org.bassis.bassis_tools.exception.CustomException;
import org.bassis.bassis_tools.string.StringUtils;

public class AopImpl {
	private static Logger logger = Logger.getLogger(AopImpl.class);

	private static class LazyHolder {
		private static final AopImpl INSTANCE = new AopImpl();
	}

	private AopImpl() {
	}

	public static final AopImpl getInstance() {
		AopImpl impl = LazyHolder.INSTANCE;
		impl.setPerform(true);
		return impl;
	}

	public static final String PREHANDLE_NAME = "preHandle";// 前置
	public static final String POSTHANDLE_NAME = "postHandle";// 完成
	public static final String AFTERCOMPLETION_NAME = "afterCompletion";// 异常
	public static final int PREHANDLE_INDEX = 0;// 前置
	public static final int POSTHANDLE_INDEX = 1;// 完成
	public static final int AFTERCOMPLETION_INDEX = 2;// 异常
	private static volatile boolean perform = true;// 正常执行

	public boolean isPerform() {
		return perform;
	}

	private void setPerform(boolean _perform) {
		AopImpl.perform = _perform;
	}

	/**
	 * aop代理执行
	 * 
	 * @param method
	 * @param phase
	 * @throws Exception
	 */
	public void Aop(Method method, int phase, Object _objs) throws Exception {
		if (!method.isAnnotationPresent(Aop.class))
			return;

		Aop aop = method.getAnnotation(Aop.class);
		String path = aop.value();
		if (null == aop || StringUtils.isEmptyString(path))
			return;

		Class<?> clz = ComponentImpl.getMapClass(path);
		Object obj = BeanFactory.getLoader(clz);
		Method[] methods = clz.getDeclaredMethods();
		// 方法筛选
		for (Method m : methods) {
			switch (phase) {
			case 1:
				// 完成
				if (m.getName().equals(POSTHANDLE_NAME) && perform) {
					try {
						m.invoke(obj, _objs);
					} catch (Exception e) {
						// TODO: handle exception
						CustomException.throwOut("aop " + clz.getName() + "|" + AopImpl.PREHANDLE_NAME, e);
					}
				}
				break;
			case 2:
				// 异常
				if (m.getName().equals(AFTERCOMPLETION_NAME)) {
					invokeMethod_afterCompletion(obj, methods, _objs);
				}
				break;
			default:
				// 前置
				if (m.getName().equals(PREHANDLE_NAME) && perform)
					invokeMethod_preHandle(obj, methods, m, _objs);
				break;
			}
		}
	}

	/**
	 * 前置
	 * 
	 * @param las
	 * @param methods
	 * @param method
	 * @return
	 */
	public void invokeMethod_preHandle(Object obj, Method[] methods, Method method, Object _objs)  throws Exception{
		try {
			Object res = method.invoke(obj, _objs);
			perform = (boolean) res;
			logger.debug("方法代理执行完成");
		} catch (Exception e) {
			// TODO: handle exception
			invokeMethod_afterCompletion(obj, methods, _objs);
			CustomException.throwOut("aop " + obj.getClass().getName() + "|" + AopImpl.PREHANDLE_NAME, e);
		}
		if (!perform) {
			invokeMethod_afterCompletion(obj, methods, _objs);
			CustomException.throwOut("aop " + obj.getClass().getName() + "|" + AopImpl.PREHANDLE_NAME);
		}
	}

	/**
	 * 异常
	 * 
	 * @param obj
	 * @param methods
	 */
	public void invokeMethod_afterCompletion(Object obj, Method[] methods, Object _objs)  throws Exception{
		// 异常调用
		for (Method m : methods) {
			if (m.getName().equals(AFTERCOMPLETION_NAME)) {
				m.invoke(obj, _objs);
			}
		}
	}
}
