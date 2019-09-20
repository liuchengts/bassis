package com.bassis.data.utlis;

import com.bassis.data.common.DBConfig;
import com.bassis.tools.exception.CustomException;
import com.bassis.tools.string.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * jdbc实际操作类
 */
public class JdbcTools {
    //连接
    private Connection conn;
    //事务锚点
    private Savepoint sp;
    //是否支持数据保存
    private Boolean savepoints;
    //数据库连接配置
    private DBConfig dbConfig;
    //当前操作单元的唯一标记
    private String no;

    protected String getNo() {
        return no;
    }

    protected void setNo(String no) {
        this.no = no;
    }

    public DBConfig getDbConfig() {
        return dbConfig;
    }

    /*********
     * 初始化操作器
     * @param dbConfig db配置
     */
    protected JdbcTools(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    private JdbcTools() {
    }

    /*********
     * 初始化数据库连接（新建一个数据库连接）
     */
    protected Connection initConnection() {
        if (this.conn != null) return this.conn;
        if (StringUtils.isEmptyString(dbConfig.getJdbcUrl()) || StringUtils.isEmptyString(dbConfig.getUserName()) || StringUtils.isEmptyString(dbConfig.getPassWord()))
            CustomException.throwOut("数据库连接参数错误");

        try {
            //加载驱动类
            Class.forName(dbConfig.getDrivers());
        } catch (ClassNotFoundException e) {
            CustomException.throwOut("数据库驱动加载异常:", e);
        }
        try {
            this.conn = DriverManager.getConnection(dbConfig.getJdbcUrl(), dbConfig.getUserName(), dbConfig.getPassWord());
            this.savepoints = this.conn.getMetaData().supportsSavepoints();
            setAutoCommit(false);
        } catch (SQLException e) {
            CustomException.throwOut("数据库连接失败:", e);
        }
        return this.conn;
    }

    /************
     * 执行sql 有事务控制
     * @param sql sql
     * @param autoCommit  为true为自动提交事务  默认为false
     * @return 返回结果
     */
    protected ResultSet executeQuery(String sql, boolean autoCommit) {
        if (StringUtils.isEmptyString(sql)) CustomException.throwOut("sql is null");
        initConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (autoCommit) commit();
        } catch (Exception e) {
            CustomException.throwOut("执行sql失败:", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                CustomException.throwOut("释放资源异常 executeQuery:", e);
            }
        }
        return rs;
    }

    /************
     * 设置事务是否自动提交
     */
    protected void setAutoCommit(Boolean autoCommit) {
        try {
            if (this.conn != null) this.conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            CustomException.throwOut("设置事务自动提交失败:", e);
        }
    }

    /************
     * 提交事务
     */
    protected void commit() {
        try {
            if (this.conn != null && this.savepoints) {
                this.conn.commit();
                this.sp = this.conn.setSavepoint();//设置事务点
            }
        } catch (SQLException e) {
            CustomException.throwOut("设置事务自动提交失败:", e);
        }
    }

    /************
     * 回滚事务
     */
    protected void rollback() {
        try {
            if (this.sp != null && this.savepoints) {
                this.conn.rollback(this.sp);//回滚至上个提交的事务点
                this.conn.commit();//这里调用事务本身的方法，不调用封装方法
                this.conn.releaseSavepoint(this.sp);//清除事务还原点
            } else {
                this.conn.rollback();
            }
        } catch (SQLException e) {
            CustomException.throwOut("事务回滚失败:", e);
        }
    }

    /************
     * 关闭数据库连接
     */
    protected void close() {
        try {
            if (this.conn != null) this.conn.close();
        } catch (SQLException e) {
            CustomException.throwOut("关闭连接失败:", e);
        }
    }

}
