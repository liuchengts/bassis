package bassis.bassis_web.work;

import java.util.List;

import com.google.gson.annotations.Expose;

public class Page<T> {
	@Expose
	private  int total;//总行数
	@Expose
	private  List<T> data;//结果
	@Expose
	private  T obj;//结果
	@Expose
	private int start;//第几页
	@Expose
	private int limit=5;//每页显示多少条
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public T getObj() {
		return obj;
	}
	public void setObj(T obj) {
		this.obj = obj;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public Page(int total, List<T> data, T obj, int start, int limit) {
		super();
		this.total = total;
		this.data = data;
		this.obj = obj;
		this.start = start;
		this.limit = limit;
	}
	public Page() {
		super();
	}
	
}
