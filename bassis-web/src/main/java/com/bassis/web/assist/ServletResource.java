package com.bassis.web.assist;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 资源操作
 */
public class ServletResource implements Serializable {
    private String path;//请求路径
    private String packagePath;//包路径 可以通过反射创建此对象
    private String resource;//请求资源名称带后缀
    private String resourceName;//请求资源名称 不带后缀
    private int resourceType;//资源类型（业务类型）
    private String controllerPath;//控制器请求路径
    private String methodPath;//方法请求路径
    private LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();//请求参数

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

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public LinkedHashMap<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(LinkedHashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    private ServletResource() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static ServletResource init(ServletAttribute attribute) {
        ServletResource resource = new ServletResource();
        resource.setPath(attribute.getRequest().getServletPath().trim());
        resource.parameters.putAll(attribute.getRequest().getParameterMap());
        return resource;
    }
}
