package  bassis.bassis_web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class RootFilter implements Filter  {
	private static Logger logger = Logger.getLogger(RootFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest req, ServletResponse res,
						 FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String url = request.getRequestURI();
			logger.info("访问到了"+url);
		chain.doFilter(request, response);
//		if(StringUtils.contains(request.getServerName(), property.getProperty("wxhost")) ||  StringUtils.contains(request.getServerName(), property.getProperty("hthost"))|| StringUtils.contains(request.getServerName(), property.getProperty("host"))){//无线
//			if(url != null && !url.equals("") && !url.equals("/")){
//				chain.doFilter(request, response);
//			}
//		}
//		if(StringUtils.contains(request.getServerName(), property.getProperty("hthost"))){//后台
//			if(url != null && !url.equals("") && !url.equals("/")){
//				chain.doFilter(request, response);
//			}else{
//				response.sendRedirect(Constants.getAppCallUrl(request)+"?"+request.getQueryString());
//			}
//		}else if(StringUtils.contains(request.getServerName(), property.getProperty("wxhost")) || StringUtils.contains(request.getServerName(), property.getProperty("host"))){//无线
//			if(url != null && !url.equals("") && !url.equals("/")){
//				chain.doFilter(request, response);
//			}else{
//				StringBuffer url1 = request.getRequestURL();
//				String host = url1.substring(0, url1.indexOf("/",8));
//				response.sendRedirect(host+"/wap/Index.wap2?"+request.getQueryString());
//			}
//		}
	}

	@Override
	public void destroy() {

	}

}
