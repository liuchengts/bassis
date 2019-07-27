package com.bassis.hibernate.tool;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Page implements Serializable{
	private int totalPage;//总页数
	private int totalRecord;//总记录条数
	@SuppressWarnings("rawtypes")
	private List data;//结果
	private int curPage;//当前页

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	@SuppressWarnings("rawtypes")
	public List getData() {
		return data;
	}

	@SuppressWarnings("rawtypes")
	public void setData(List data) {
		this.data = data;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

}
