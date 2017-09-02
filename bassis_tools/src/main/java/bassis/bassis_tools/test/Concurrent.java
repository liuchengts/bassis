package bassis.bassis_tools.test;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import bassis.bassis_tools.reflex.Reflection;

public class Concurrent {
	private static Logger logger = Logger.getLogger(Concurrent.class);
	final static ExecutorService service = Executors.newCachedThreadPool();

	/**
	 * 测试某个方法的并发情况
	 * 
	 * @param count
	 * @param method
	 * @param obj
	 */
	public static void test(int count, String method_name, Class<?> las) {
		long t = System.currentTimeMillis();
		try {
			final Method method = Reflection.getMethod(las, method_name);
			final Object obj = las.newInstance();
			final CountDownLatch cdOrder = new CountDownLatch(1);//将军
			final CountDownLatch cdAnswer = new CountDownLatch(count);//小兵 10000
			for (int i = 0; i < count; i++) {
				Runnable runnable = new Runnable() {
					public void run() {
						try {
							cdOrder.await(); // 处于等待状态
							try {
								long t1 = System.currentTimeMillis();
								method.invoke(obj, method.getParameters());
								logger.info("执行时间>>>>>>" + (System.currentTimeMillis() - t1));
							} catch (Exception e) {
								// XXX: handle exception
								logger.error("测试方法执行异常，测试程序退出：", e);
								return;
							}
							cdAnswer.countDown(); // 任务执行完毕，cdAnswer减1。

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				service.execute(runnable);// 为线程池添加任务
			}
			cdOrder.countDown();//-1
			cdAnswer.await();
		} catch (Exception e) {
			// XXX Auto-generated catch block
			e.printStackTrace();
			logger.error("异常：", e);
		}
		service.shutdown();
		logger.info("测试完成:执行时间>>>>>>" +  (System.currentTimeMillis() - t));
		System.exit(0);
	}
}
