package org.bs.interceptor;

import bassis.bassis_bean.annotation.Component;
import bassis.bassis_web.interceptor.Interceptor;

@Component("/interceptor/privilege")
public class PrivilegeInterceptor implements Interceptor{

	@Override
	public boolean preHandle(Object obj) {
		// TODO Auto-generated method stub
		System.out.println("执行了preHandle");
		return true;
	}

	@Override
	public void postHandle(Object obj) {
		// TODO Auto-generated method stub
		System.out.println("执行了postHandle");
	}

	@Override
	public void afterCompletion(Object obj) {
		// TODO Auto-generated method stub
		
	}

	

}
