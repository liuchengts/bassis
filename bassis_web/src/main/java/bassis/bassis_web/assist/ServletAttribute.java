package bassis.bassis_web.assist;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 包含当前请求的会话内容
 *
 */
public class ServletAttribute {
	private HttpSession session;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext servletContext;

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	private ServletAttribute() {
		super();
	}

	public static ServletAttribute init(ServletContext servletContext, HttpServletRequest request,
			HttpServletResponse response) {
		return new ServletAttribute(servletContext, request, response);
	}

	private ServletAttribute(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) {
		super();
		this.session = request.getSession();
		this.request = request;
		this.response = response;
		this.servletContext = servletContext;

		// 获得容器版本
		servletContext.getServerInfo();
		// 项目跟路径，去掉/为项目名getContextPath()无法获得项目名
		servletContext.getContextPath();
		// servlet容器支持的Servlet API的版本号
		servletContext.getMajorVersion();
		// 资源根路径
		servletContext.getResourcePaths("/");

	}

}
