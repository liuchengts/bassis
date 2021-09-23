package com.bassis.boot.web.assist;

import com.bassis.boot.common.HttpPage;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.json.GsonUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 视图返回
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
     * @param attribute       servlet参数
     * @param servletResource servlet的资源
     * @return 返回 ServletView
     */
    public static ServletView init(ServletAttribute attribute, ServletResource servletResource) {
        ServletView view = new ServletView();
        view.setRequest(attribute.getRequest());
        view.setResponse(attribute.getResponse());
        view.setServletResource(servletResource);
        view.viewConfig();
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
     * 直接输出字符串
     *
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
        this.getResponse().setContentType("application/json; charset=utf-8");
        try {
            this.getRequest().setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            CustomException.throwOut("controller request CharacterEncoding error ", e);
        }
    }

    public static final String SUCCESS = "SUCCESS";// 成功
    public static final String ERROR = "ERROR";// 失败


    /**
     * 重定向到指定页面
     *
     * @param code 错误码
     */
    public void redirect(int code) {
        switch (code) {
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                this.redirect(HttpPage.ERROR_500);
                break;
            case HttpServletResponse.SC_SERVICE_UNAVAILABLE:
                this.redirect(HttpPage.ERROR_503);
                break;
            case HttpServletResponse.SC_NOT_FOUND:
                this.redirect(HttpPage.ERROR_404);
                break;
            default:
                this.redirect(HttpPage.INDEX);
                break;
        }
    }

    /**
     * 根据路径重定向
     *
     * @param path 路径
     */
    public void redirect(String path) {
        try {
            this.getResponse().sendRedirect(path);
        } catch (IOException e) {
            CustomException.throwOut("response redirect error", e);
        }
    }
}
