package bassis.bassis_bean.annotation.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.log4j.Logger;

import bassis.bassis_bean.BeanFactory;
import bassis.bassis_bean.annotation.Autowired;
import bassis.bassis_tools.exception.CustomException;
import bassis.bassis_tools.gc.GcUtils;
import bassis.bassis_tools.reflex.ReflexUtils;
import bassis.bassis_tools.string.StringUtils;

public class AutowiredImpl {
	private static Logger logger = Logger.getLogger(AutowiredImpl.class);
	private static class LazyHolder {
		private static final AutowiredImpl INSTANCE = new AutowiredImpl();
	}

	private AutowiredImpl() {
	}

	public static final AutowiredImpl getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * 给外部调用注入资源 没有标记自动注入注解的字段将被忽略
	 * 
	 * @param clz
	 * @param args
	 */
	public static synchronized void analyseFieldIOC(Object obj, Map<String, Object> mapRds) throws Exception {
		fieldIOC(obj, mapRds);
	}

	/**
	 * 为web方式提供ioc功能，需要带上注入的参数
	 * 
	 * @param clz
	 * @param args
	 */
	private static  void fieldIOC(Object obj, Map<String, Object> mapRds) throws Exception {
		Field[] fields = null;
		try {
			fields = obj.getClass().getDeclaredFields();
			if (null == fields || fields.length <= 0)
				return;

			for (Field field : fields) {
				if (!field.isAnnotationPresent(Autowired.class))
					continue;

				ioc(obj, field, mapRds);
			}
		} finally {
			GcUtils.getInstance();
		}

	}
	/**
	 * 提供给非web方式的ioc 不给任何参数，自动初始化参数注入
	 * 
	 * @param clz
	 */
	public static synchronized void analyseFieldIOC(Class<?> clz) throws Exception {
		logger.debug(clz.getName());
		Field[] fields = null;
		try {
			Object obj = BeanFactory.getLoader(clz);
			fields = obj.getClass().getDeclaredFields();
			if (null == fields || fields.length <= 0)
				return;

			for (Field field : fields) {
				if (!field.isAnnotationPresent(Autowired.class)){
					continue;
				}
				ioc(obj, field);
			}
		} finally {
			GcUtils.getInstance();
		}
	}
	
	/**
	 * 进行参数不为null验证
	 * 
	 * @return
	 */
	private static boolean verifyNotNull(Field field, Object[] valueArr) {
		if (!field.isAnnotationPresent(Autowired.class)) {
			return true;
		}
		Autowired rannotation = field.getAnnotation(Autowired.class);
		String verify = rannotation.verify();
		if (StringUtils.isEmptyString(verify))
			return true;

		if ("NOTNULL".equals(verify) && (null == valueArr || valueArr.length <= 0 || null == valueArr[0])) {
			CustomException.throwOut(" @Autowired field:" + field.getName() + " is not null");
			return false;
		}
		return true;
	}

	/**
	 * 进行正则验证 此功能暂时放弃
	 * 
	 * @return
	 */
	// private static boolean verifyMatcher(Field field,Object value) {
	// if(!field.isAnnotationPresent(Autowired.class)){
	// return true;
	// }
	// Autowired rannotation = field.getAnnotation(Autowired.class);
	// String verify=rannotation.verify();
	// if(StringUtils.isEmptyString(verify))
	// return true;
	//
	// Pattern pattern = Pattern.compile(verify);
	// Matcher matcher = pattern.matcher(value);
	// // 字符串是否与正则表达式相匹配
	// boolean rs = matcher.matches();
	// return false;
	// }
	/**
	 * 字段属性注入
	 * 
	 * @param clz
	 * @param field
	 */
	private static void ioc(Object obj, Field field, Map<String, Object> mapRds) throws Exception {
		String name = field.getName();
		field.setAccessible(true);
		if (null == mapRds || mapRds.isEmpty()) {
			verifyNotNull(field, null);
			return;
		}
		// 这里要对复合类型进行判断比如user.name这种
		if (ReflexUtils.isWrapClass(field.getType().getName())) {
			String _name = obj.getClass().getSimpleName().toLowerCase() + "." + name;
			if (!mapRds.containsKey(name) && !mapRds.containsKey(_name)) {
				verifyNotNull(field, null);
				return;
			}

			Object[] valueArr = (Object[]) mapRds.get(name);
			if (null == valueArr || valueArr.length <= 0) {
				valueArr = (Object[]) mapRds.get(_name);
				name = _name;
			}
			// 进行字段属性验证
			if (!verifyNotNull(field, valueArr))
				return;

			Object value = valueArr[0];
			field.set(obj, value);
			logger.debug(name + "字段注入成功");
		} else {
			// 不是基础数据类型
			Object _obj = BeanFactory.getClassLoaderToObject(field.getType().getName());
			// 将实例放入当前调用对象
			field.set(obj, _obj);
			//将新的对象进行二层ioc注入，每次都会进入下一层ioc
			fieldIOC(_obj, mapRds);
		}
	}

	/**
	 * 字段属性注入
	 * 这个方法不支持dao注入
	 * @param clz
	 * @param field
	 */
	private static void ioc(Object obj, Field field) throws Exception {
		field.setAccessible(true);
		Class<?> cla = field.getType();
		Autowired annotation = field.getAnnotation(Autowired.class);
		// 输出注解上的属性
		String value = annotation.value();
		Class<?> clas = annotation.clas();
		Object fobj=null;
		// 只有当lcas是一个接口 并且注解有参数
		if (cla.isInterface()) {
			if(null!=clas && clas!=Object.class){
				fobj=BeanFactory.getLoader(clas);
			}else if(!StringUtils.isEmptyString(value)){
				Class<?> claImpl = BeanFactory.getClassLoader(value);
				fobj=BeanFactory.getBeanObj(claImpl);
			}else{
				CustomException.throwOut("@Autowired not resource");
			}
		} else if(!ReflexUtils.isWrapClass(field.getType().getName())){
			//不是基础类型
			if(null!=clas && clas!=Object.class){
				fobj=BeanFactory.getLoader(clas);
			}else if(!StringUtils.isEmptyString(value)){
				Class<?> claImpl = BeanFactory.getClassLoader(value);
				fobj=BeanFactory.getBeanObj(claImpl);
			}else{
				//如果是其他类型 没有参数声明 直接new当前类型
				fobj=BeanFactory.getLoader(cla);
			}
		}else if(!ReflexUtils.isWrapClass_Pack(field.getType().getName())){
			//是基础类型的包装类型
			fobj=BeanFactory.getLoader(cla);
			ReflexUtils.invokeMethod(obj, field.getName(), null);
		}else{
			//基本数据类型
		}
		if(null==fobj){
			return;
		}
		field.set(obj, fobj);
		logger.debug(field.getName() + "字段请求参数注入成功");
	}
}
