package com.bassis.web.assist;

import java.util.HashMap;
import java.util.Map;

import bassis.bassis_bean.ReferenceDeclaration;
import com.bassis.tools.string.StringUtils;

/**
 * 资源操作
 */
public class ServletResource {
	private String path;//请求路径
	private String packagePath;//包路径 可以通过反射创建此对象
	private String resource;//请求资源名称带后缀
	private String resourceName;//请求资源名称 不带后缀
	private String resourceSuffix;//请求资源后缀
	private int resourceType;//资源类型（业务类型）
	private String controllerPath;//控制器请求路径
	private String methodPath;//方法请求路径
	private Map<String, Object> map=new HashMap<String, Object>();//请求参数
	
	public String getControllerPath() {
		return controllerPath;
	}
	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}
	public String getMethodPath() {
		return methodPath;
	}
	public void setMethodPath(String methodPath) {
		this.methodPath = methodPath;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPackagePath() {
		return packagePath;
	}
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceSuffix() {
		return resourceSuffix;
	}
	public void setResourceSuffix(String resourceSuffix) {
		this.resourceSuffix = resourceSuffix;
	}
	public int getResourceType() {
		return resourceType;
	}
	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
	}
	
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	private ServletResource() {
		super();
	}
	@SuppressWarnings("unchecked")
	public static ServletResource init(ServletAttribute attribute) {
		ServletResource resource=new ServletResource();
		resource.setPath(attribute.getRequest().getServletPath().trim());
		resource.setPackagePath(getPackagePath(resource.getPath()));
		resource.setResourceSuffix(getRequestSuffix(resource.getPath()));
		resource.setResourceType(getRequestType(resource.getResourceSuffix()));
		resource.setMap(attribute.getRequest().getParameterMap());
		//其他属性在这里set进来
		return resource;
	}
	/**
	 * 获得请求返回视图类型
	 * @param path
	 * @return
	 */
	private static int getRequestType(String resourceSuffix) {
			return ReferenceDeclaration.getViewType(resourceSuffix);
	}
	/**
	 * 获得请求后缀
	 * @param path
	 * @return
	 */
	private static String getRequestSuffix(String path) {
		int index = path.indexOf('.');
		if (index > 0) {
			return path.substring(index + 1);
		}
		return null;
	}
	/**
	 * 获得包路径
	 * @param path
	 * @return
	 */
	private static String getPackagePath(String path){
		int last = path.lastIndexOf('.');
		if(last>0)
		path = path.substring(0, last);
		
		path=StringUtils.replace(path, "/", ".");
		path=ReferenceDeclaration.getControllerPackage()+path;
		return path;
	}
}
