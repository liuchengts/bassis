package com.bassis.data.utlis;

import com.bassis.tools.exception.CustomException;
import com.bassis.tools.utils.IDUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jdbctools 管理器
 */
public class JdbcSupervisor {
    private static Logger logger = Logger.getLogger(JdbcSupervisor.class);

    private static class LazyHolder {
        private static final JdbcSupervisor INSTANCE = new JdbcSupervisor();
    }

    public static JdbcSupervisor getInstance() {
        return LazyHolder.INSTANCE;
    }

    //jdbctools 存储器  唯一id / 实例
    private static Map<String, JdbcTools> storage = new ConcurrentHashMap<>();
    //jdbctools 组存储器   组名 / 唯一id 、是否可用
    private static Map<String, Map<String, Boolean>> group = new ConcurrentHashMap<>();
    //最小连接数
    private final static int MIN_COUNT = 10;
    //最大连接数
    private final static int MAX_COUNT = 20;

    /**
     * 创建一个jdbc操作工具
     *
     * @param prefix   db标识
     * @param jdbcUrl  jdbc连接地址
     * @param userName 用户名
     * @param passWord 密码
     * @param drivers  驱动
     * @return 返回工具类
     */
    private JdbcTools createJdbcTools(String prefix, String jdbcUrl, String userName, String passWord, String drivers) {
        if (storage.size() > MAX_COUNT) CustomException.throwOut("数据库连接数达到上限:" + MAX_COUNT);
        String no = IDUtils.generateNo(prefix);
        JdbcTools jdbcTools = new JdbcTools(no, jdbcUrl, userName, passWord, drivers);
        storage.put(no, jdbcTools);
        Map<String, Boolean> map = new HashMap<>();
        if (group.containsKey(prefix)) {
            map = group.get(prefix);
        }
        map.put(no, true);
        group.put(prefix, map);
        return jdbcTools;
    }

    /**
     * 初始化连接池
     *
     * @param prefix   db标识
     * @param jdbcUrl  jdbc连接地址
     * @param userName 用户名
     * @param passWord 密码
     * @param drivers  驱动
     */
    public void initCreateJdbcTools(String prefix, String jdbcUrl, String userName, String passWord, String drivers) {
        int i = 0;
        while (i < MIN_COUNT) {
            createJdbcTools(prefix, jdbcUrl, userName, passWord, drivers);
            i++;
        }
    }

    /**
     * 随机获得一个可用的操作工具
     *
     * @param prefix db标识
     * @return 返回工具类
     */
    public synchronized JdbcTools getRandomJdbcTools(String prefix) {
        String no = group.get(prefix).entrySet().stream().filter(Map.Entry::getValue).findFirst().map(Map.Entry::getKey).orElse("");
        JdbcTools jdbcTools = storage.get(no);
        //将其置为占用状态防止其他人使用
        group.get(prefix).put(no, false);
        return jdbcTools;
    }

    /**
     * 回收一个操作工具
     *
     * @param jdbcTools 要回收的工具类
     * @return 返回工具类
     */
    public synchronized void recyclJdbcTools(JdbcTools jdbcTools) {
        for (Map.Entry<String, Map<String, Boolean>> gmap : group.entrySet()) {
            Map<String, Boolean> map = gmap.getValue();
            if (map.containsKey(jdbcTools.getNo())) {
                map.put(jdbcTools.getNo(), true);
                group.put(gmap.getKey(), map);
                break;
            }
        }
    }
}
