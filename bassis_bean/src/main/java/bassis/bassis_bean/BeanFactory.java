package bassis.bassis_bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import bassis.bassis_bean.scan.ScanConfig;
import org.bassis.bassis_tools.properties.FileProperties;
import org.bassis.bassis_tools.reflex.ReflexUtils;
public class BeanFactory {
	private static Logger logger = Logger.getLogger(BeanFactory.class);
	
	private static class LazyHolder {
		private static final BeanFactory INSTANCE = new BeanFactory();
	}

	private BeanFactory() {
		
	}

	public static final BeanFactory getInstance() {
		return LazyHolder.INSTANCE;
	}
	//bean实例
	static Map<Class<?>, Object> mapObjs = new HashMap<Class<?>, Object>();
	//bean实例
	static Map<String, Object> mapStringObjs = new HashMap<String, Object>();
	private static ClassLoader classLoader = null;
	static FileProperties properties;
	/**
	 * 获得读取器
	 * @return
	 */
	public static FileProperties getProperties() {
		return properties;
	}
	/**
	 * 获得加载器
	 * 
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		return classLoader;
	}
	/**
	 * 使用加载器加载class 放入工厂存储区中
	 * 
	 * @return
	 */
	public static Class<?> getClassLoader(String path) throws Exception{
		Class<?> clz =classLoader.loadClass(path);
		Object obj =getBeanObj(clz);
		if(null!=obj)
			return obj.getClass();
		
		obj =clz.newInstance();
		setMapObjs(clz, obj);
		return clz;
	}
	/**
	 * 使用加载器加载class 放入工厂存储区中
	 * 
	 * @return
	 */
	public static Class<?> getClassLoader(Class<?> clz) throws Exception{
		Object obj =getBeanObj(clz);
		if(null!=obj)
			return obj.getClass();
		
		Class<?> clzs =classLoader.loadClass(clz.getName());
		obj =clzs.newInstance();
		setMapObjs(clz, obj);
		return clzs;
	}
	/**
	 * 使用加载器加载class 放入工厂存储区中
	 * 
	 * @return
	 */
	public static Object getLoader(Class<?> clz) throws Exception{
		Object obj =getBeanObj(clz);
		if(null!=obj)
			return obj;
		
		Class<?> clzs =classLoader.loadClass(clz.getName());
		obj =clzs.newInstance();
		setMapObjs(clz, obj);
		return obj;
	}
	static {
		classLoader = ReflexUtils.getClassLoader();
		// 初始化properties文件读取器
		properties = FileProperties.getInit();
		//加载bean默认配置
		properties.read(ReferenceDeclaration.BEAN_PROPERTIES);
	}

	/**
	 * 启动扫描
	 * @param path
	 */
	public static void init(String path) {
		ScanConfig.setScanRoot(path);
	}
	/**
	 * 启动扫描
	 * @param path
	 */
	public static void init() {
		init(ReferenceDeclaration.getScanRoot());
	}
	public static Map<Class<?>, Object> getMapObjs() {
		return mapObjs;
	}

	/**
	 * 如果出现重复 直接覆盖
	 * @param key
	 * @param value
	 */
	public static void setMapObjs(Class<?> key,Object value) {
		mapObjs.put(key, value);
	}
	/**
	 * 根据class获得obj实例
	 * 
	 * @param clz
	 * @return
	 */
	public static Object getBeanObj(Class<?> clz) {
		if (mapObjs.containsKey(clz))
			return mapObjs.get(clz);

		return null;
	}
	/**
	 * 移除一个bean
	 * @param key
	 */
	public static void remove(Class<?>  key) {
		mapObjs.remove(key);
	}
	//+===================以下是待验证方法 bean的关注点应该在obj内存地址上 而不是class实例上 以下方法会进行bean的整改===========
	/**
	 * 使用加载器加载class 放入工厂存储区中，这将会根据clz创建一个新的obj对象
	 * 
	 * @return
	 */
	public static Object getClassLoaderStorageToObject(Class<?> clz) throws Exception{
		Object	obj =getClassLoaderToObject(clz);
		mapStringObjs.put(obj.toString(), obj);
		return obj;
	}
	/**
	 * 使用加载器加载class 不放入工厂存储区中，这将会根据clz创建一个新的obj对象
	 * 
	 * @return
	 */
	public static Object getClassLoaderToObject(Class<?> clz) throws Exception{
		Object	obj =classLoader.loadClass(clz.getName()).newInstance();
		return obj;
	}
	/**
	 * 使用加载器加载class 得到obj，并且不放入存储区
	 * 
	 * @return
	 */
	public static Object getClassLoaderToObject(String path) throws Exception{
		Class<?> clz =classLoader.loadClass(path);
		return clz.newInstance();
	}
}
