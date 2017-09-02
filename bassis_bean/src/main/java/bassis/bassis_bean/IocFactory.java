package bassis.bassis_bean;

import java.util.Map;

import bassis.bassis_bean.annotation.impl.AutowiredImpl;
import bassis.bassis_tools.exception.CustomException;

public class IocFactory {
	private static class LazyHolder {
		private static final IocFactory INSTANCE = new IocFactory();
	}

	private IocFactory() {
	}

	public static final IocFactory getInstance() {
		return LazyHolder.INSTANCE;
	}
	/**
	 * IOC
	 * 只对自动注入注解
	 * @param clz
	 * @param args
	 */
	public static void analyseFieldIOC(Object obj, Map<String, Object> mapRds) {
		try {
			AutowiredImpl.analyseFieldIOC(obj, mapRds);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CustomException.throwOut("IOC failure ",e);
		}
	}
}
