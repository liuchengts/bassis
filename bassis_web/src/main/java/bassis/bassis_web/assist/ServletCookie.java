package  bassis.bassis_web.assist;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * cookie 操作
 */
public class ServletCookie {
	private Cookie[] cookies;

	public Cookie[] getCookies() {
		return cookies;
	}

	public void setCookies(Cookie[] cookies) {
		this.cookies = cookies;
	}

	/**
	 * 使用无参方法需要手动进行属性set值 否则一些方法将无法使用
	 */
	public ServletCookie() {
		super();
	}

	public static ServletCookie init(ServletAttribute attribute) {
		ServletCookie resource = new ServletCookie();
		HttpServletRequest request = attribute.getRequest();
		// 其他属性在这里set进来
		resource.setCookies(request.getCookies());
		// List<Cookie> _urlCookies=getUrlCookies(request);
		// if()
		return resource;
	}

	/**
	 * 根据当前request对象获得name指定cookie 必须保证先执行了 init方法
	 * 
	 * @param name
	 * @return
	 */
	public String getCookieValue(String name) {
		if (cookies == null || cookies.length == 0)
			return null;
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name))
				return cookies[i].getValue();
		}
		return null;
	}

	/****
	 * 根据cookies和name获得cookie内容
	 * 
	 * @param cookies
	 * @param name
	 * @return
	 */
	public String getCookieValue(Cookie[] cookies, String name) {
		if (cookies == null || cookies.length == 0)
			return null;
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name))
				return cookies[i].getValue();
		}
		return null;
	}

	/**
	 * 添加cookie
	 * 
	 * @param cookies
	 * @param cookie
	 */
	public static void addCookie(List<Cookie> cookies, Cookie cookie) {
		for (int i = 0; i < cookies.size(); i++) {
			if (cookies.get(i).getName().equals(cookie.getName())) {
				cookies.remove(i);
				break;
			}
		}
		cookies.add(cookie);
	}
}
