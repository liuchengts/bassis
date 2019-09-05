package com.bassis.boot.web.assist;

import com.bassis.tools.json.GsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 视图返回
 *
 */
public class ServletView implements Serializable {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private boolean sendType;// false 表示转发 true表示重定向
	private Object rlt;// 数据
	private ServletResource servletResource;

	public ServletResource getServletResource() {
		return servletResource;
	}

	public void setServletResource(ServletResource servletResource) {
		this.servletResource = servletResource;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean isSendType() {
		return sendType;
	}

	public void setSendType(boolean sendType) {
		this.sendType = sendType;
	}

	public Object getRlt() {
		return rlt;
	}

	public void setRlt(Object rlt) {
		this.rlt = rlt;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public ServletView() {
		super();
	}


	/**
	 * 初始化一个视图 不会进行转发动作
	 *
	 * @param attribute
	 * @param servletResource
	 * @return 返回 ServletView
	 */
	public static ServletView init(ServletAttribute attribute, ServletResource servletResource) {
		ServletView view = new ServletView();
		view.setRequest(attribute.getRequest());
		view.setResponse(attribute.getResponse());
		view.setServletResource(servletResource);
		return view;
	}

	/**
	 * 直接输出视图内的数据为json
	 */
	public void outJson() throws Exception {
		String json = GsonUtils.exposeObjectToJson(this.getRlt());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultData", json);
		PrintWriter out = response.getWriter();
		out.write(Objects.requireNonNull(GsonUtils.objectToJson(map)));
		out.flush();
		out.close();
	}

	/**
	 * 直接输出json
	 *
	 * @param obj 要转json的对象
	 */
	public void outJson(Object obj) throws Exception {
		String json = GsonUtils.exposeObjectToJson(obj);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultData", json);
		PrintWriter out = response.getWriter();
		out.write(Objects.requireNonNull(GsonUtils.objectToJson(map)));
		out.flush();
		out.close();
	}

	/**
	 * 直接输出json
	 *
	 * @param obj 要转json的对象
	 */
	public void outJson_root(Object obj) throws Exception {
		String json = GsonUtils.objectToJson(obj);
		PrintWriter out = response.getWriter();
		assert json != null;
		out.write(json);
		out.flush();
		out.close();
	}

	/**
	 * 直接输出字符串
	 * @param str 要输出的对象
	 */
	public void outString(String str) throws Exception {
		PrintWriter out = response.getWriter();
		out.write(str);
		out.flush();
		out.close();
	}



	/**
	 * 配置视图
	 */
	public void viewConfig() {
		this.getResponse().setCharacterEncoding("utf-8");
        this.getResponse().setContentType("com.lc.grpc.service.application/json; charset=utf-8");
	}


	/***
	 * 这里需要读取配置将数据放到这里
	 */
	public static final String error_500 = "error/500.html";// 500地址
	public static final String error_503 = "error/503.html";// 500地址
	public static final String error_404 = "error/404.html";// 500地址
	public static final String SUCCESS = "SUCCESS";// 成功
	public static final String ERROR = "ERROR";// 失败
}
