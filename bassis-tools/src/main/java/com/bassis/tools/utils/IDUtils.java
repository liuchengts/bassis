package com.bassis.tools.utils;

import java.util.concurrent.ThreadLocalRandom;

public class IDUtils {
    /**
     * 生成随机编号 18位
     *
     * @param prefix 前缀
     * @return 返回编号  前缀 +系统时间戳+线程id+3位随机数
     */
    public static String generateNo(String prefix) {
        Long threadId = Thread.currentThread().getId();
        String no = prefix + System.nanoTime() + threadId + ThreadLocalRandom.current().nextInt(0, 1000);
        int c = no.length() - 18;
        if (c > 0) {
            no = no.substring(0, no.length() - c);
        }
        return no;
    }
}
