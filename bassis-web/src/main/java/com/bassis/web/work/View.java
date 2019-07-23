package com.bassis.web.work;

import java.io.Serializable;

/**
 * 提供View的设置
 *
 */
public class View implements Serializable{
	private String sendUrl;// 发送地址
	private boolean sendType;// false 表示转发 true表示重定向
	private Object rlt;// 数据
	public String getSendUrl() {
		return sendUrl;
	}
	public void setSendUrl(String sendUrl) {
		this.sendUrl = sendUrl;
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
	/**
	 * 定义一个视图动作
	 * @param sendUrl  视图地址
	 * @param sendType false 表示转发 true表示重定向
	 * @param rlt  数据
	 */
	public View(String sendUrl, Boolean sendType, Object rlt) {
		super();
		this.sendUrl = null==sendUrl?"":sendUrl;
		this.sendType = null==sendType?false:sendType;
		this.rlt =rlt;
	}
	public View() {
		super();
	}
	
}
