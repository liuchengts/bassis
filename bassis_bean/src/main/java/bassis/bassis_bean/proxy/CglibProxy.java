package bassis.bassis_bean.proxy;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 基于cglib类的代理
 * 
 * @author ytx
 *
 */
public class CglibProxy implements MethodInterceptor {
	private static Logger logger = Logger.getLogger(CglibProxy.class);
	private Object[] parameter;
	private Class<?> target;
	private Object obj;
	
	/**
	 * 获得代理的结果 obj对象
	 * @return
	 */
	public Object getObj() {
		return obj;
	}

	private void setObj(Object obj) {
		this.obj = obj;
	}

	/**
	 * 获得代理的class
	 * @return
	 */
	public Class<?> getTarget() {
		return target;
	}

	private void setTarget(Class<?> target) {
		this.target = target;
	}
    /**
     * 获得参数
     * @return
     */
	public Object[] getParameter() {
		return parameter;
	}

	private void setParameter(Object[] parameter) {
		this.parameter = parameter;
	}

	/**
	 * 获得一个新的代理对象
	 * 
	 * @return
	 */
	public static CglibProxy CreateCglibProxy() {
		return new CglibProxy();
	}

	@SuppressWarnings("rawtypes")
	public  Object getInstance(Class clazz, CglibProxy _proxy) {
		if (null == _proxy) {
			_proxy = new CglibProxy();
		}
		_proxy.setTarget(clazz);
		return _proxy.getProxy(clazz);
	}

	@SuppressWarnings("rawtypes")
	private Object getProxy(Class clazz) {
		Enhancer enhancer = new Enhancer();
		// 设置需要创建子类的类
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		Object o=enhancer.create();
		this.setObj(o);
		return o;
	}
    /**
     * obj 方式的代理不会生成新的子类字节码
     * @param obj
     * @param _proxy
     * @return
     */
	@SuppressWarnings("rawtypes")
	public  Object getInstance(Object obj, CglibProxy _proxy) {
		if (null == _proxy) {
			_proxy = new CglibProxy();
		}
		_proxy.setTarget(obj.getClass());
		return _proxy.getProxy(obj);
	}

	@SuppressWarnings("rawtypes")
	private Object getProxy(Object obj) {
		Enhancer enhancer = new Enhancer();
		// 设置需要创建子类的类
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		this.setObj(obj);
		return obj;
	}

	// 实现MethodInterceptor接口方法
	public  Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// 通过代理类调用父类中的方法
		this.setParameter(args);
		Object result = proxy.invokeSuper(obj, args);
		method.invoke(result, args);
		return result;
	}

}
