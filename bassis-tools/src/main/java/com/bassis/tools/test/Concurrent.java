package com.bassis.tools.test;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bassis.tools.exception.CustomException;
import com.bassis.tools.reflex.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 并发测试包
 */
public class Concurrent {
    private static Logger logger = LoggerFactory.getLogger(Concurrent.class);
    final static ExecutorService service = Executors.newCachedThreadPool();

    /**
     * 测试某个方法的并发
     *
     * @param count          并发数量
     * @param las            类
     * @param method_name    方法名
     * @param parameterTypes 方法参数类型
     */
    public static void testMethod(int count, Class<?> las, String method_name, Class<?>... parameterTypes) {
        long t = System.currentTimeMillis();
        try {
            final Method method = Reflection.getMethod(false,las, method_name, parameterTypes);
            final Object obj = las.newInstance();
            final CountDownLatch cdOrder = new CountDownLatch(1);//将军
            final CountDownLatch cdAnswer = new CountDownLatch(count);//小兵 10000
            for (int i = 0; i < count; i++) {
                Runnable runnable = () -> {
                    try {
                        cdOrder.await(); // 处于等待状态
                        try {
                            long t1 = System.currentTimeMillis();
                            method.invoke(obj, (Object[]) method.getParameters());
                            logger.info("执行时间>>>>>>" + (System.currentTimeMillis() - t1));
                        } catch (Exception e) {
                            CustomException.throwOut("测试方法执行异常，测试程序退出", e);
                            return;
                        }
                        cdAnswer.countDown(); // 任务执行完毕，cdAnswer减1。

                    } catch (Exception e) {
                        CustomException.throwOut("测试线程异常", e);
                    }
                };
                service.execute(runnable);// 为线程池添加任务
            }
            cdOrder.countDown();//-1
            cdAnswer.await();
        } catch (Exception e) {
            CustomException.throwOut("异常", e);
        }
        service.shutdown();
        logger.info("测试完成:执行时间>>>>>>" + (System.currentTimeMillis() - t));
        System.exit(0);
    }
}
