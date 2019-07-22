//package com.bassis.web.assist;
//
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import bassis.bassis_bean.ReferenceDeclaration;
//import com.bassis.tools.exception.CustomException;
//import com.bassis.tools.json.GsonUtils;
//import com.bassis.tools.string.StringUtils;
//
///**
// * 视图返回
// *
// */
//public class ServletView {
//	private String sendUrl;// 发送地址
//	private HttpServletRequest request;
//	private HttpServletResponse response;
//	private boolean sendType;// false 表示转发 true表示重定向
//	private Object rlt;// 数据
//	private Boolean motion;// 定义发送地址的资源动作 null 表示直接走http true表示跳转到控制器
//							// false表示跳转到视图
//	private ServletResource servletResource;
//
//	public ServletResource getServletResource() {
//		return servletResource;
//	}
//
//	public void setServletResource(ServletResource servletResource) {
//		this.servletResource = servletResource;
//	}
//
//	public Boolean getMotion() {
//		return motion;
//	}
//
//	public void setMotion(Boolean motion) {
//		this.motion = motion;
//	}
//
//	public String getSendUrl() {
//		return sendUrl;
//	}
//
//	public void setSendUrl(String sendUrl) {
//		this.sendUrl = createSendUrl(this, sendUrl);
//		;
//	}
//
//	public HttpServletRequest getRequest() {
//		return request;
//	}
//
//	public void setRequest(HttpServletRequest request) {
//		this.request = request;
//	}
//
//	public boolean isSendType() {
//		return sendType;
//	}
//
//	public void setSendType(boolean sendType) {
//		this.sendType = sendType;
//	}
//
//	public Object getRlt() {
//		return rlt;
//	}
//
//	public void setRlt(Object rlt) {
//		this.rlt = rlt;
//	}
//
//	public HttpServletResponse getResponse() {
//		return response;
//	}
//
//	public void setResponse(HttpServletResponse response) {
//		this.response = response;
//	}
//
//	public ServletView() {
//		super();
//	}
//
//	private ServletView(String sendUrl, ServletAttribute attribute, boolean sendType, Object rlt) {
//		super();
//		this.sendUrl = sendUrl;
//		this.request = attribute.getRequest();
//		this.response = attribute.getResponse();
//		this.sendType = sendType;
//		this.rlt = rlt;
//		this.motion = false;
//	}
//
//	/**
//	 * 根据参数生成url http action 页面
//	 *
//	 * @param url
//	 * @return
//	 */
//	public static String createSendUrl(ServletView view, String url) {
//		if (StringUtils.isEmptyString(url))
//			return null;
//
//		// 直接出去到http
//		if (StringUtils.startsWith(url, "http") || StringUtils.startsWith(url, "https")) {
//			if (!xenu(view, url)) {
//				return null;
//			}
//			view.setMotion(null);
//			return url;
//		}
//		String urlSuffix = StringUtils.endslastIndexOf_str(url, ".");
//		// 判断是否转发到action
//		if (StringUtils.lastIndexOf(ReferenceDeclaration.getControllerSuffix(), urlSuffix) != -1) {
//			if (!xenu(view, url)) {
//				return null;
//			}
//			view.setMotion(true);
//			return url;
//		}
//		// 到页面 如果没有配置视图前缀 默认转发到/WEB-INF/下
//		url = ReferenceDeclaration.getViewUrlPrefix() + url;
//		if (StringUtils.lastIndexOf(url, ".") == -1) {
//			// 没有发现视图后缀 默认获得配置的视图后缀第一个后缀
//			url += StringUtils.indexOf_str(ReferenceDeclaration.getViewUrlSuffix(), ".", 2);
//		}
//		view.setMotion(false);
//		return url;
//	}
//
//	/**
//	 * 死链检查
//	 *
//	 * @param view
//	 * @param url
//	 * @return
//	 */
//	private static boolean xenu(ServletView view, String url) {
//		// 判断死链
//		if (StringUtils.indexOf(view.getRequest().getServletPath(), url) != -1) {
//			CustomException.throwOut("Repair Broken Links sendUrl ");
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 初始化一个视图 不会进行转发动作
//	 *
//	 * @param sendUrl
//	 * @param attribute
//	 * @param sendType
//	 * @param rlt
//	 */
//	public static ServletView init(ServletAttribute attribute, ServletResource servletResource) {
//		ServletView view = new ServletView();
//		view.setRequest(attribute.getRequest());
//		view.setResponse(attribute.getResponse());
//		view.setServletResource(servletResource);
//		return view;
//	}
//
//	/**
//	 * 返回json
//	 *
//	 * @param obj
//	 */
//	public void requestJson(Object obj) throws Exception {
//		String json = GsonUtils.exposeObjectToJson(obj);
//		this.setRlt(json);
//		send();
//	}
//
//	/**
//	 * 直接输出视图内的数据为json
//	 *
//	 * @param obj
//	 */
//	public void outJson() throws Exception {
//		String json = GsonUtils.exposeObjectToJson(this.getRlt());
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("resultData", json);
//		PrintWriter out = response.getWriter();
//		out.write(GsonUtils.objectToJson(map));
//		out.flush();
//		out.close();
//	}
//
//	/**
//	 * 直接输出json
//	 *
//	 * @param obj
//	 */
//	public void outJson(Object obj) throws Exception {
//		String json = GsonUtils.exposeObjectToJson(obj);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("resultData", json);
//		PrintWriter out = response.getWriter();
//		out.write(GsonUtils.objectToJson(map));
//		out.flush();
//		out.close();
//	}
//
//	/**
//	 * 直接输出json
//	 *
//	 * @param obj
//	 */
//	public void outJson_root(Object obj) throws Exception {
//		String json = GsonUtils.objectToJson(obj);
//		PrintWriter out = response.getWriter();
//		out.write(json);
//		out.flush();
//		out.close();
//	}
//
//	/**
//	 * 直接输出字符串
//	 *
//	 */
//	public void outString(String str) throws Exception {
//		PrintWriter out = response.getWriter();
//		out.write(str);
//		out.flush();
//		out.close();
//	}
//
//	/**
//	 * 初始化一个视图 同时进行转发动作
//	 *
//	 * @param sendUrl
//	 * @param attribute
//	 * @param sendType
//	 * @param rlt
//	 * @return
//	 */
//	public static void initSend(String sendUrl, ServletAttribute attribute, boolean sendType, Object rlt)
//			throws Exception {
//		send(new ServletView(sendUrl, attribute, sendType, rlt));
//	}
//
//	/**
//	 * 进行转发动作
//	 *
//	 * @param view
//	 * @throws Exception
//	 */
//	public static void send(ServletView view) throws Exception {
//		if (view.isSendType()){
//			view.getResponse().sendRedirect(view.getSendUrl());
//			return;
//		}
//		// 这里需要对rlt数据做反射处理获得map
//		Map<String, Object> map = new HashMap<String, Object>();
//		if (null != view.getRlt()) {
//			String key = view.getRlt().getClass().getSimpleName().toLowerCase();
//			map.put(key, view.getRlt());
//		}
//		view.getRequest().setAttribute("resultData", map);
//		view.getRequest().getRequestDispatcher(view.getSendUrl()).forward(view.getRequest(), view.getResponse());
//	}
//
//	/**
//	 * 配置视图
//	 *
//	 * @param viewType
//	 *            不进行json输出的处理
//	 * @throws Exception
//	 */
//	public void sendConfig(int viewType) throws Exception {
//		this.getResponse().setCharacterEncoding("utf-8");
//		// 进行视图类型匹配
//		switch (viewType) {
//		case ServletView.json:
//			// 以html返回
//			this.getResponse().setContentType("text/javascript; charset=utf-8");
//			break;
//		case ServletView.html:
//			// 以html返回
//			this.getResponse().setContentType("text/html; charset=utf-8");
//			break;
//		case ServletView.wap:
//			// 以wap返回
//			this.getResponse().setContentType("text/html; charset=utf-8");
//			break;
//		case ServletView.h:
//			// 以h返回
//			this.getResponse().setContentType("text/html; charset=utf-8");
//			break;
//		default:
//			// 以文本返回
//			this.getResponse().setContentType("text/plain; charset=utf-8");
//			break;
//		}
//	}
//
//	/**
//	 * 进行转发动作
//	 *
//	 * @param view
//	 * @throws Exception
//	 */
//	public void send() throws Exception {
//		send(this);
//	}
//
//	/***
//	 * 这里需要读取配置将数据放到这里
//	 */
//	public static final int json = 0;// json
//	public static final int html = 1;// html
//	public static final int wap = 3;// wap
//	public static final int h = 4;// h
//	public static final String error_500 = "error/500";// 500地址
//	public static final String error_503 = "error/503";// 500地址
//	public static final String error_404 = "error/404.html";// 500地址
//	public static final String SUCCESS = "SUCCESS";// 成功
//	public static final String ERROR = "ERROR";// 失败
//}
