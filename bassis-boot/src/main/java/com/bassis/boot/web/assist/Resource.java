package com.bassis.boot.web.assist;

public class Resource {
	private String tId;//进程id
	private ServletAttribute servletAttribute;
	private ServletClient servletClient;
	private ServletCookie servletCookie;
	private ServletResource servletResource;

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public ServletAttribute getServletAttribute() {
		return servletAttribute;
	}

	public void setServletAttribute(ServletAttribute servletAttribute) {
		this.servletAttribute = servletAttribute;
	}

	public ServletClient getServletClient() {
		return servletClient;
	}

	public void setServletClient(ServletClient servletClient) {
		this.servletClient = servletClient;
	}

	public ServletCookie getServletCookie() {
		return servletCookie;
	}

	public void setServletCookie(ServletCookie servletCookie) {
		this.servletCookie = servletCookie;
	}

	public ServletResource getServletResource() {
		return servletResource;
	}

	public void setServletResource(ServletResource servletResource) {
		this.servletResource = servletResource;
	}

	public Resource(ServletAttribute servletAttribute, ServletClient servletClient, ServletCookie servletCookie,
			ServletResource servletResource) {
		super();
		this.servletAttribute = servletAttribute;
		this.servletClient = servletClient;
		this.servletCookie = servletCookie;
		this.servletResource = servletResource;
	}

}
