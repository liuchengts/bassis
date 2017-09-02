package bassis.bassis_tools.reflex;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ReflexUtils {
	static Set<Class<?>> basisTypes=new HashSet<Class<?>>();
	static Set<Class<?>> basis_pack_Types=new HashSet<Class<?>>();
	static ClassLoader classLoader = null;
	static Thread  thread= null;
	static{
		basis_pack_Types.add(Date.class);
		basis_pack_Types.add(Integer.class);
		basis_pack_Types.add(String.class);
		basis_pack_Types.add(Double.class);
		basis_pack_Types.add(Float.class);
		basis_pack_Types.add(Boolean.class);
		basis_pack_Types.add(Long.class);
		basis_pack_Types.add(Byte.class);
		basis_pack_Types.add(File.class);
		//==基础数据类型
		basisTypes.add(int.class);
		basisTypes.add(double.class);
		basisTypes.add(float.class);
		basisTypes.add(boolean.class);
		basisTypes.add(long.class);
		basisTypes.add(byte.class);
		basisTypes.add(short.class);
		thread=Thread.currentThread();
		classLoader = thread.getContextClassLoader();
	}
	/**
	 * 获得当前线程
	 * @return
	 */
	public static Thread getThread() {
		return thread;
	}
	/**
	 * 获得加载器
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		return classLoader;
	}
	/**
	 * 检查是否是基础数据类型（包装类型）
	 * 
	 * @param clz
	 * @return 只有当不是基础数据类型返回false
	 */
	public static boolean isWrapClass(Class<?> clz) {
		boolean fag=basisTypes.contains(clz);
		if(!fag){
			fag=isWrapClass_Pack(clz);
		}
		return  fag;
	}
	/**
	 * 检查是否是基础数据包装类
	 * 
	 * @param clz
	 * @return 只有当不是基础数据类型包装类返回false
	 */
	public static boolean isWrapClass_Pack(Class<?> clz) {
		return  basis_pack_Types.contains(clz);
	}
	/**
	 * 检查是否是基础数据类型（包装类型）
	 * 
	 * @param clz
	 * @return 只有当不是基础数据类型返回false
	 */
	public static boolean isWrapClass(String path) throws Exception {
		try {
			return isWrapClass(classLoader.loadClass(path));
		} catch (Exception e) {
			// TODO: handle exception
			return true;
		}
	}
	/**
	 * 检查是否是基础数据包装类
	 * 
	 * @param clz
	 * @return 只有当不是基础数据类型装类返回false
	 */
	public static boolean isWrapClass_Pack(String path) throws Exception {
		try {
			return isWrapClass_Pack(classLoader.loadClass(path));
		} catch (Exception e) {
			// TODO: handle exception
			return true;
		}
	}
	/** 
     * 获得对象属性的值 
     */  
    @SuppressWarnings("unchecked")  
    public static  Object invokeMethod(Object owner, String methodName,Object[] args) throws Exception {  
        Class<?> ownerClass = owner.getClass();  
        methodName = methodName.substring(0, 1).toUpperCase()+ methodName.substring(1); 
        Method method = null;  
        try {  
            method = ownerClass.getMethod("get" + methodName);  
        } catch (SecurityException e) {  
        } catch (NoSuchMethodException e) {  
            return " can't find 'get" + methodName + "' method";  
        }  
        return method.invoke(owner);  
    }
    
    /**
     * 检查是否存在class
     * @param path
     * @return
     * @throws Exception
     */
	public static boolean isClass(String path) {
		try {
			classLoader.loadClass(path);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
}
