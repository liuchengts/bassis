package  bassis.bassis_web.assist;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import bassis.bassis_tools.string.StringUtils;

public class ServletClient {
	private Map<String,String> headers =new HashMap<String,String>();
	private String referer;
	private String device;// 设备类型
	private String chromeKernel;// 浏览器内核
	private String ipAddress;// ip地址
	private Date visitTime;// 访问时间

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getChromeKernel() {
		return chromeKernel;
	}

	public void setChromeKernel(String chromeKernel) {
		this.chromeKernel = chromeKernel;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}
	private ServletClient() {
		super();
	}
	public static ServletClient init(ServletAttribute attribute) {
		ServletClient resource=new ServletClient();
		HttpServletRequest request=attribute.getRequest();
		//其他属性在这里set进来
		resource.setIpAddress(resource.getIpAddr(request));
		resource.setDevice(resource.getDeviceValue(request));
		resource.setReferer(request.getHeader("referer"));
		Enumeration<?> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			resource.getHeaders().put(name, request.getHeader(name));
		}
		return resource;
	}
	/**
	 * 判断是pc还是手机
	 * @param request
	 * @return
	 */
	private String getDeviceValue(HttpServletRequest request) {
		if(StringUtils.isEmptyString(request.getHeader("X-Mobile"))){
			
		}else{
			if(StringUtils.isEmptyString(request.getHeader("xmobile"))){
				
			}
		}
			
			
			
		return chromeKernel;
	}
	/**
	 * 获得ip地址
	 * @param request
	 * @return
	 */
	private String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		// ipAddress = this.getRequest().getRemoteAddr();
		ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}

		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
}
