package bassis.bassis_hibernate.tool;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
public interface Dao {
	public void commit();
	public void close();
	public void rollBack();
	public void delete(Object dpo);
	public void save(Object dpo);
	public Object locate(Class<?> clz, Serializable id);
	public Object locate(Class<?> clz,Object value[],String column[]);
	public Object locateLock(Class<?> clz,Object value[],String column[]);
	public Object locateLock(Class<?> clz, Serializable id);
	public Session getSession();
	public Session getSession_tx();
	public void flush();
	public void clear();
	public List<Object> SQLQuery(String sql);
	public int SQLQueryCount(String sql);
	public List<Object> CriteriaQuery(Class<?> clz, Object[] value, String[] column);
	public int CriteriaQueryCount(Class<?> clz, Object[] value, String[] column);
	public Page getPage(Criteria query, int pageSize, int curPage);
	public Page CriteriaQueryPage(Class<?> clz, Object[] value, String[] column, int pageSize, int curPage, String[] ascs,
			String[] descs);
	public String getDbIndex();
}
