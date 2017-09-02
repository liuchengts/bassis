package bassis.bassis_bean.aop;

/**
 * aop接口
 */
public interface Aop {

	/**
	 * 在业务处理器处理请求之前被调用 如果返回false 从当前的aop往回执行所有aop的afterCompletion(),再退出aop链
	 * 如果返回true 执行下一个aop,直到所有的aop都执行完毕 再执行被拦截的方法 ,
	 * 从最后一个aop往回执行所有的postHandle() 接着再从最后一个aop往回执行所有的afterCompletion()
	 */
	public boolean preHandle(Object obj);

	/**
	 * 在业务处理器处理请求执行完成后
	 */
	public void postHandle(Object obj);

	/**
	 * 在Servlet完全处理完请求后被调用,可用于清理资源等
	 * 
	 * 当有aop抛出异常时,会从当前aop往回执行所有的aop的afterCompletion()
	 */
	public void afterCompletion(Object obj);
}
