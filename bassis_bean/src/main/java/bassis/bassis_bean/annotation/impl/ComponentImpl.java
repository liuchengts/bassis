package bassis.bassis_bean.annotation.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import bassis.bassis_bean.BeanFactory;
import bassis.bassis_bean.annotation.Component;
import bassis.bassis_bean.scan.ScanTask;
import bassis.bassis_tools.exception.CustomException;
import bassis.bassis_tools.string.StringUtils;

/**
 * 处理 Component.class注解
 */
public class ComponentImpl {
	private static Logger logger = Logger.getLogger(ComponentImpl.class);

	private static class LazyHolder {
		private static final ComponentImpl INSTANCE = new ComponentImpl();
	}

	private ComponentImpl() {
	}

	public static final ComponentImpl getInstance() {
		return LazyHolder.INSTANCE;
	}

	static Set<Class<?>> scanPackageList = ScanTask.getInstance().getPackageList();
	// 包请求注解路径/包路径
	static Map<String, Class<?>> mapClass = new HashMap<String, Class<?>>();
	// 包请求注解路径 、 方法注解路径/方法名称
	static Map<String, Map<String, Method>> mapMethod = new HashMap<String, Map<String, Method>>();

	public static Map<String, Class<?>> getMapClass() {
		return mapClass;
	}

	public static Map<String, Map<String, Method>> getMapMethod() {
		return mapMethod;
	}

	/**
	 * 根据请求路径获取类
	 * 
	 * @param key
	 * @return
	 */
	public static Class<?> getMapClass(String key) {
		if (!mapClass.containsKey(key))
			return null;

		return mapClass.get(key);
	}

	/**
	 * 根据请求路径获取方法
	 * 
	 * @param key
	 * @return
	 */
	public static Map<String, Method> getMapMethod(String key) {
		if (!mapMethod.containsKey(key))
			return null;

		return mapMethod.get(key);
	}
	/**
	 * putclass
	 * 
	 * @param key
	 * @return
	 */
	private static void putMapClass(String key,Class<?> clz) throws Exception{
		mapClass.put(key, clz);
		BeanFactory.getClassLoader(clz);
	}
	// 只处理当前实现类的注解
	static {
		logger.debug("@Component分析开始");
		mapClass.clear();
		mapMethod.clear();
		for (Class<?> clz : scanPackageList) {
			try {
				if (clz.isAnnotationPresent(Component.class))
					analyse(clz);
			} catch (Exception e) {
				// TODO: handle exception
				CustomException.throwOut("@Component分析异常：" ,e);
			}
			
		}
	}

	/**
	 * 开始分析注解
	 * 
	 * @param clz
	 */
	private static void analyse(Class<?> clz) throws Exception{
		logger.debug(clz.getName());
		Component annotation = clz.getAnnotation(Component.class);
		// 输出注解上的属性
		String path = annotation.value();
		if (StringUtils.isEmptyString(path)) {
			// 如果没有给定注解值 那么让它以当前包的上级包路径+类名称 为请求路径 如 org.bs.aop.UserAoporg
			// 请求路径为 aop.UserAoporg
			path = StringUtils.subStringCustom(clz.getName(), clz.getSimpleName(), ".") + "." + clz.getSimpleName();
		}
		if (mapClass.containsKey(path)) {
			CustomException.throwOut(" @Component repeat:" + path);
		}
		logger.debug("path:" + path);
		putMapClass(path, clz);
		analyseMethods(path, clz);
		//自动注入
		AutowiredImpl.analyseFieldIOC(clz);
	}
    
	/**
	 * 方法注解分析
	 * 
	 * @param path
	 * @param clz
	 */
	private static void analyseMethods(String path, Class<?> clz) {
		// 分析方法
		Method[] methods = clz.getDeclaredMethods();
		Map<String, Method> map = getMapMethod(path);
		if (null == map || map.isEmpty()) {
			map = new HashMap<String, Method>();
		}
		for (Method method : methods) {
			// 以当方法名称 为请求路径
			map.put(method.getName(), method);
		}
		mapMethod.put(path, map);
	}
}
