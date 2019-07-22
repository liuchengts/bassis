package com.bassis.web.work;

/**
 * 对外输出的数据模型
 * @author ytx
 *
 */
public class ResultBean {
	private boolean status;// 成功或失败
	private String code;// 返回编码
	private String msg;// 消息
	private Object res;// 数据

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getRes() {
		return res;
	}

	public void setRes(Object res) {
		this.res = res;
	}

	public ResultBean(boolean status, String code, String msg, Object res) {
		super();
		this.status = status;
		this.code = code;
		this.msg = msg;
		this.res = res;
	}

	public ResultBean() {
		super();
	}

}
