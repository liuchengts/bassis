package com.bassis.tools.gc;

/**
 * gc计数器
 */
public class GcUtils {
    private static class LazyHolder {
        private static final GcUtils INSTANCE = new GcUtils();
    }

    private GcUtils() {
    }

    public static final GcUtils getInstance() {
        add();
        return LazyHolder.INSTANCE;
    }

    private static final long serialVersionUID = 1L;
    private static long index = 0;
    /**
     * 计数上限
     */
    private static final long COUNT = 10;

    /**
     * 增加计数器值
     *
     * @return 返回计数器当前值
     */
    private static long add() {
        index++;
        if (index >= COUNT) {
            gc();
        }
        return index;
    }

    /**
     * 通知gc
     */
    private static void gc() {
        System.gc();
        index = 0;
    }
}
