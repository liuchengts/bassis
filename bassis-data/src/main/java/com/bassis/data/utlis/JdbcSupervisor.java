package com.bassis.data.utlis;

import com.bassis.data.common.DBConfig;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.string.StringUtils;
import com.bassis.tools.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jdbctools 管理器
 */
public class JdbcSupervisor {
    private static Logger logger = LoggerFactory.getLogger(JdbcSupervisor.class);

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
    //db配置存储 组名/配置类
    private static Map<String, DBConfig> configMap = new HashMap<>();
    //最小连接数
    private final static int MIN_COUNT = 10;
    //最大连接数
    private final static int MAX_COUNT = 20;

    /**
     * 创建一个jdbc操作工具
     *
     * @param dbConfig 配置信息
     * @return 返回工具类
     */
    private JdbcTools createJdbcTools(DBConfig dbConfig) {
        assert dbConfig != null;
        if (storage.size() > MAX_COUNT) CustomException.throwOut("数据库连接数达到上限:" + MAX_COUNT);
        String no = IDUtils.generateNo(dbConfig.getPrefix());
        JdbcTools jdbcTools = new JdbcTools(dbConfig);
        jdbcTools.setNo(no);
        storage.put(no, jdbcTools);
        Map<String, Boolean> map = new HashMap<>();
        if (group.containsKey(dbConfig.getPrefix())) {
            map = group.get(dbConfig.getPrefix());
        }
        map.put(no, true);
        group.put(dbConfig.getPrefix(), map);
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
        DBConfig dbConfig;
        if (configMap.containsKey(prefix)) {
            dbConfig = configMap.get(prefix);
        } else {
            dbConfig = new DBConfig(prefix, jdbcUrl, userName, passWord, drivers);
            configMap.put(prefix, dbConfig);
        }
        int i = 0;
        while (i < MIN_COUNT) {
            createJdbcTools(dbConfig);
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
        JdbcTools jdbcTools;
        if (StringUtils.isEmptyString(no)) {
            jdbcTools = createJdbcTools(configMap.get(prefix));
        } else {
            jdbcTools = storage.get(no);
            //将其置为占用状态防止其他人使用
            group.get(prefix).put(no, false);
        }

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
                //需要更新唯一操作标记
                //清除当前操作标记存储信息
                map.remove(jdbcTools.getNo());
                storage.remove(jdbcTools.getNo());
                //获取新的操作标记
                jdbcTools.setNo(IDUtils.generateNo(jdbcTools.getDbConfig().getPrefix()));
                //存储新的信息
                map.put(jdbcTools.getNo(), true);
                storage.put(jdbcTools.getNo(), jdbcTools);
                group.put(gmap.getKey(), map);
                break;
            }
        }
    }


    /**
     * 清理无用的空闲连接至最小连接数
     *
     */
    public synchronized void cleanTools() {
        if (storage.size() > MAX_COUNT){

        }
    }
}
