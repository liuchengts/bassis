package bassis.bassis_hibernate.annotation.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import bassis.bassis_hibernate.annotation.Data;
import bassis.bassis_hibernate.connection.DaoImpl;
import org.bassis.tools.gc.GcUtils;
import org.bassis.tools.string.StringUtils;

public class DataImpl {
	private static Logger logger = Logger.getLogger(DataImpl.class);
	private static Set<String> methodName = new HashSet<String>();
	private static Map<String, Set<DaoImpl>> mapDB = new HashMap<>();
	static {
		methodName.add("update");
		methodName.add("insert");
		methodName.add("add");
		methodName.add("save");
		methodName.add("delete");
		methodName.add("del");
		methodName.add("remove");
		methodName.add("rem");
	}
    /**
     * 根据进程id获得db信息 
     * @param tid 为当前声明db的obj内存地址
     * @return
     */
	public static Set<DaoImpl> getDAOs(String tid) {
		return mapDB.containsKey(tid) ? mapDB.get(tid) : new HashSet<>();
	}
	/**
	 * 进行db的ioc
	 * @param obj
	 * @param mName
	 * @throws Exception
	 */
	public void dbIoc(Object obj, String mName) throws Exception{
		String tid=obj.toString();
		Set<DaoImpl> daoList=getDAOs(tid);
		fieldIOC(obj,daoList, mName);
		mapDB.put(tid, daoList);
	}

	/**
	 * data的注解实现
	 * @param obj
	 * @param setDao
	 * @param mName
	 * @throws Exception
	 */
	private void fieldIOC(Object obj, Set<DaoImpl> setDao, String mName) throws Exception {
		Field[] fields = null;
		try {
			fields = obj.getClass().getDeclaredFields();
			if (null == fields || fields.length <= 0)
				return;

			for (Field field : fields) {
				if (!field.isAnnotationPresent(Data.class))
					continue;

				ioc(obj, field, setDao, mName);
			}
		} finally {
			GcUtils.getInstance();
		}

	}

	/**
	 * db属性注入
	 * 
	 * @param obj
	 * @param field
	 * @param setDao
	 * @param mName
	 * @throws Exception
	 */
	private void ioc(Object obj, Field field, Set<DaoImpl> setDao, String mName) throws Exception {
		field.setAccessible(true);
		Data annotation = field.getAnnotation(Data.class);
		// 输出注解上的属性
		String value = annotation.value();
		DaoImpl dao = null;
		// 只有当lcas是一个接口
		if (!field.getType().isInterface()) {
			return;
		}
		if (!StringUtils.isEmptyString(value)) {
			dao = new DaoImpl(value);
		} else {
			dao = new DaoImpl();
		}
		if(!StringUtils.isEmptyString(mName)){
			for (String s : methodName) {
				if (mName.startsWith(s)) {
					dao.getSession_tx();
					break;
				}
			}
		}
		setDao.add(dao);
		field.set(obj, dao);
		logger.debug(field.getName() + "字段注入db成功");
	}

}
