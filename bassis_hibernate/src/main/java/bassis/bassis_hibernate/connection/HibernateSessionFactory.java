package bassis.bassis_hibernate.connection;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateSessionFactory {
	private static final String KEY = "default";
	private String INDEX;
	private StandardServiceRegistry registry;
	private SessionFactory sessionFactory;
	private Session session;
	private Session session_tx;
	private Transaction transaction;
	private static final Map<String, HibernateSessionFactory> mapHibernateSessionFactory = new HashMap<String, HibernateSessionFactory>();

	public String getINDEX() {
		return INDEX;
	}

	protected synchronized static HibernateSessionFactory getHibernateSessionFactory(String key) {
		HibernateSessionFactory factory = mapHibernateSessionFactory.get(key);
		if (null == factory) {
			factory = new HibernateSessionFactory();
			factory.init(key);
		}
		return factory;
	}

	protected synchronized static HibernateSessionFactory getHibernateSessionFactory() {
		return getHibernateSessionFactory(KEY);
	}

	private void put(String name) {
		System.out.println("new " + name);
		try {
			// 创建会话工厂
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			// TODO: handle exception
			StandardServiceRegistryBuilder.destroy(registry);
			e.printStackTrace();
		}
		INDEX = name;
		mapHibernateSessionFactory.put(name, this);
	}

	private void init() {
		// Hibernate 5.0.2Final与之前创建方法不同，之前创建方法会报各种错误，查官方文档
		// 注册服务;
		registry = new StandardServiceRegistryBuilder().configure().build();
		put(KEY);
	}

	private void init(String name) {
		if (KEY.equals(name)) {
			init();
			return;
		}
		// Hibernate 5.0.2Final与之前创建方法不同，之前创建方法会报各种错误，查官方文档
		// 注册服务;
		registry = new StandardServiceRegistryBuilder().configure("hibernate_" + name + ".cfg.xml").build();
		put(name);
	}

	protected synchronized SessionFactory getSessionFactory() {
		return getSessionFactory(KEY);
	}

	protected synchronized SessionFactory getSessionFactory(String name) {
		HibernateSessionFactory hibernateSessionFactory = mapHibernateSessionFactory.get(name);
		if (null == hibernateSessionFactory || null == hibernateSessionFactory.sessionFactory) {
			init(name);
		} else {
			return hibernateSessionFactory.sessionFactory;
		}
		return getSessionFactory(name);
	}

	protected Session getSession() {
		if (null == session) {
			// 会话对象
			session = getSessionFactory(INDEX).openSession();
		}
		return session;
	}

	protected Session getSession_tx() {
		if (null == session_tx) {
			// 会话对象
			session_tx = getSessionFactory(INDEX).openSession();
			getTransaction();
		}
		return session_tx;
	}

	protected Transaction getTransaction() {
		if (null == transaction) {
			// 开启事物
			transaction = getSession().beginTransaction();
		}
		return transaction;
	}

	/**
	 * 提交事务
	 */
	protected void commit() {
		getTransaction().commit();// 提交事物
	}

	/**
	 * 回滚事务
	 */
	protected void rollback() {
		getTransaction().rollback();// 回滚事务
	}

	/**
	 * 关闭当前会话
	 */
	protected void close() {
		getSession().close();// 关闭会话
	}

	/**
	 * 关闭session连接
	 */
	protected void sessionClose() {
		getSessionFactory(INDEX).close();// 关闭会话工厂
	}

	/**
	 * 提交当前事务并关闭session连接
	 */
	protected void destory() {
		commit();// 提交事物
		close();
		sessionClose();
	}
}