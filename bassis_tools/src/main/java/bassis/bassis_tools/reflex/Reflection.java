package bassis.bassis_tools.reflex;

import java.lang.reflect.Method;

import bassis.bassis_tools.exception.CustomException;

public class Reflection {
	public static Method getMethod(Class<?> las, String methodName) throws Exception {
		Method[] methods = las.getDeclaredMethods();
		Method method = null;
		// 方法筛选
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				method = m;
				break;
			}
		}
		return method;
	}

	/**
	 * 代执行方法
	 * 
	 * @param method_name
	 *            方法名
	 * @param las
	 *            类名
	 * @return 返回方法执行后的对象obj
	 */
	public static Object invoke(String method_name, Class<?> las) throws Exception {
		Method method = getMethod(las, method_name);
		return method.invoke(las.newInstance(), method.getParameters());
	}
	/**
	 * 进行代理执行
	 * 
	 * @param las
	 * @param method
	 * @return
	 */
	public static Object invokeMethod(Object obj, Method method) throws Exception {
		Object res = null;
		try {
			res = method.invoke(obj, method.getParameters());
		} catch (Exception e) {
			// TODO: handle exception
			res = CustomException.parsing(e.getMessage(), e, res);
		}
		return res;
	}

	/**
	 * 进行代理执行
	 * 
	 * @param las
	 * @param method
	 * @return
	 */
	public static Object invokeMethod(Object obj, Method method, Object... args) throws Exception {
		Object res = null;
		try {
			res = method.invoke(obj, args);
		} catch (Exception e) {
			// TODO: handle exception
			res = CustomException.parsing(e.getMessage(), e, res);
		}
		return res;
	}
}
