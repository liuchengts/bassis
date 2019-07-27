package com.bassis.hibernate.connection;

import java.io.Serializable;
import java.util.List;

import com.bassis.hibernate.tool.Dao;
import com.bassis.hibernate.tool.Page;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class DaoImpl implements Dao {
	private HibernateSessionFactory factory;

	public DaoImpl(String name) {
		factory = HibernateSessionFactory.getHibernateSessionFactory(name);
	}

	public DaoImpl() {
		factory = HibernateSessionFactory.getHibernateSessionFactory();
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		factory.commit();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		factory.close();
	}

	@Override
	public void rollBack() {
		// TODO Auto-generated method stub
		factory.rollback();
	}

	@Override
	public void delete(Object dpo) {
		// TODO Auto-generated method stub
		getSession_tx().delete(dpo);
	}

	@Override
	public void save(Object dpo) {
		// TODO Auto-generated method stub
		getSession_tx().saveOrUpdate(dpo);
	}

	@Override
	public Object locate(Class<?> clz, Serializable id) {
		// TODO Auto-generated method stub
		return getSession().get(clz, id);
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	public Object locate(Class<?> clz, Object[] value, String[] column) {
		// TODO Auto-generated method stub
		Criteria query = getSession().createCriteria(clz);
		for (int i = 0; i < value.length; i++) {
			query.add(Restrictions.eq(column[i], value[i]));
		}
		query.setMaxResults(1);
		List list = query.list();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	public Object locateLock(Class<?> clz, Object[] value, String[] column) {
		// TODO Auto-generated method stub
		Session session = getSession();
		Criteria query = session.createCriteria(clz);
		for (int i = 0; i < value.length; i++) {
			query.add(Restrictions.eq(column[i], value[i]));
		}
		query.setMaxResults(1);
		List list = query.list();
		if (list.size() > 0) {
			Object rlt = list.get(0);
			session.lock(rlt, LockMode.UPGRADE);
			return rlt;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object locateLock(Class<?> clz, Serializable id) {
		// TODO Auto-generated method stub
		return getSession().get(clz, id, LockMode.UPGRADE);
	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return factory.getSession();
	}

	@Override
	public Session getSession_tx() {
		// TODO Auto-generated method stub
		return factory.getSession_tx();
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		getSession_tx().flush();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		getSession().clear();
		getSession_tx().clear();
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<Object> SQLQuery(String sql) {
		// TODO Auto-generated method stub
		return getSession().createSQLQuery(sql).list();
	}

	@Override
	public int SQLQueryCount(String sql) {
		// TODO Auto-generated method stub
		List<Object> list = SQLQuery(sql);
		return Integer.valueOf(list.get(0).toString());
	}

	@SuppressWarnings("deprecation")
	@Override
	public int CriteriaQueryCount(Class<?> clz, Object[] value, String[] column) {
		// TODO Auto-generated method stub
		Session session = getSession();
		Criteria query = session.createCriteria(clz);
		for (int i = 0; i < value.length; i++) {
			query.add(Restrictions.eq(column[i], value[i]));
		}
		return (Integer) query.uniqueResult();
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<Object> CriteriaQuery(Class<?> clz, Object[] value, String[] column) {
		// TODO Auto-generated method stub
		Session session = getSession();
		Criteria query = session.createCriteria(clz);
		for (int i = 0; i < value.length; i++) {
			query.add(Restrictions.eq(column[i], value[i]));
		}
		return query.list();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Page CriteriaQueryPage(Class<?> clz, Object[] value, String[] column, int pageSize, int curPage,
                                  String[] ascs, String[] descs) {
		// TODO Auto-generated method stub
		Session session = getSession();
		Criteria query = session.createCriteria(clz);
		for (int i = 0; i < value.length; i++) {
			query.add(Restrictions.eq(column[i], value[i]));
		}
		if (null != ascs) {
			for (int i = 0; i < ascs.length; i++) {
				query.addOrder(Order.asc(ascs[i]));
			}
		}
		if (null != descs) {
			for (int i = 0; i < descs.length; i++) {
				query.addOrder(Order.desc(descs[i]));
			}
		}
		Page page = getPage(query, pageSize, curPage);
		page.setData(query.list());
		return page;
	}

	@Override
	public Page getPage(Criteria query, int pageSize, int curPage) {
		Page page = new Page();
		query.setProjection(Projections.rowCount());
		int count = (Integer) query.uniqueResult();
		int totalPage = getTotalPage(count, pageSize, curPage);
		page.setTotalPage(totalPage);
		page.setCurPage(getCurPage(totalPage, curPage));
		page.setTotalRecord(count);
		query.setProjection(null);
		query.setMaxResults(pageSize);
		query.setFirstResult((page.getCurPage() - 1) * pageSize);
		return page;
	}

	private int getTotalPage(int total, int pageSize, int curPage) {
		if (curPage == 0)
			curPage = 1;
		return (total / pageSize) + (total % pageSize == 0 ? 0 : 1);
	}

	private int getCurPage(int totalPage, int curPage) {
		if (curPage > totalPage)
			curPage = totalPage;
		return curPage;
	}

	@Override
	public String getDbIndex() {
		// TODO Auto-generated method stub
		return factory.getINDEX();
	}
}
