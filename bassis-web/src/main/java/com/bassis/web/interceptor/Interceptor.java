package com.bassis.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器接口
 */
public interface Interceptor {

    /**
     * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
     * 如果返回true 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
     * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
     */
    boolean preHandle(HttpServletRequest servletRequest, HttpServletResponse servletResponse);

    /**
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作 可在View中加入数据，比如当前时间
     */
    void postHandle(HttpServletRequest servletRequest, HttpServletResponse servletResponse);

    /**
     * 在Servlet完全处理完请求后被调用,可用于清理资源等
     * <p>
     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
     */
    void afterCompletion(HttpServletRequest servletRequest, HttpServletResponse servletResponse);
}
