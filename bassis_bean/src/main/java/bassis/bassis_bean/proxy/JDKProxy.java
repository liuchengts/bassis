package bassis.bassis_bean.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

/**
 * 基于接口的代理
 * 
 * @author ytx
 *
 */
public class JDKProxy implements InvocationHandler {
	private Logger logger = Logger.getLogger(JDKProxy.class);
	private static Class target;

	private JDKProxy() {
		// 不让外部调用
	}

	// 这里的参数o就是要代理的对象
	public static Object getInstance(Class o) {
		JDKProxy pm = new JDKProxy();
		pm.target = o;// 赋值,设置这个代理对象
		// 通过Proxy的方法创建代理对象，第一个参数是要代理对象的ClassLoader装载器
		// 第二个参数是要代理对象实现的所有接口
		// 第三个参数是实现了InvocationHandler接口的对象
		// 此时的result就是一个代理对象，代理的是o
		Object result = Proxy.newProxyInstance(o.getClass().getClassLoader(), o.getInterfaces(), pm);
		return result;
	}
	/**
	 * Object proxy：被代理的对象 Method method：要调用的方法 Object args[]：方法调用时所需要的参数
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		Object result = method.invoke(target, args);
		return result;
	}

}
