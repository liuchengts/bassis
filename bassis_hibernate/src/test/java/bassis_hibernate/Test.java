package bassis_hibernate;

import bassis.bassis_hibernate.connection.DaoImpl;
import bassis.bassis_hibernate.tool.Dao;

public class Test {
//	private static void init() {
//		new HibernateSessionFactory().init("taoke");
//		new HibernateSessionFactory().init();
//	}
	public static void q() {
		Dao dao=new DaoImpl();
		Dao daotaoke=new DaoImpl("taoke");
		int a=dao.SQLQueryCount("select count(1) from order_detail");
		System.out.println("db:"+dao.getDbIndex()+"| "+a);
		int a2=daotaoke.SQLQueryCount("select count(1) from goods_info");
		System.out.println("db:"+daotaoke.getDbIndex()+"| "+a2);
		
	}
	public static void main(String[] args) {
//		Concurrent.test(100, "q", Test.class);
		q();
	}
	
}
