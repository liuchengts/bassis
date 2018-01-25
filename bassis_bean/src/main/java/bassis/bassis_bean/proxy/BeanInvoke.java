package bassis.bassis_bean.proxy;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import bassis.bassis_bean.BeanFactory;
import bassis.bassis_bean.annotation.impl.AopImpl;
import bassis.bassis_tools.exception.CustomException;

public class BeanInvoke {
	private static Logger logger = Logger.getLogger(BeanInvoke.class);

	/**
	 * 进行代理执行，如果对象有aop方法 会按照顺序执行
	 * 
	 * @param las
	 * @param method
	 * @return
	 */
	public static Object invokeMethod(Object obj, Method method, Object resource) throws Exception {
		AopImpl impl = AopImpl.getInstance();
		impl.Aop(method, AopImpl.PREHANDLE_INDEX, resource);
		if (!impl.isPerform()) {
			return null;
		}
		Object res = null;
		try {
			res = method.invoke(obj, (Object[]) method.getParameters());
			logger.debug("方法代理执行完成");
		} catch (Exception e) {
			// TODO: handle exception
			impl.Aop(method, AopImpl.AFTERCOMPLETION_INDEX, resource);
			res = CustomException.parsing(e.getMessage(), e, res);
		}
		impl.Aop(method, AopImpl.POSTHANDLE_INDEX, resource);
		return res;
	}

	
}
