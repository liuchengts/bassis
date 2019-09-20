package com.bassis.data.common;

import java.io.Serializable;

/**
 * 核心配置
 */
public class DBConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    String prefix;
    String jdbcUrl;
    String userName;
    String passWord;
    String drivers;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDrivers() {
        return drivers;
    }

    public void setDrivers(String drivers) {
        this.drivers = drivers;
    }

    public DBConfig(String prefix, String jdbcUrl, String userName, String passWord, String drivers) {
        this.prefix = prefix;
        this.jdbcUrl = jdbcUrl;
        this.userName = userName;
        this.passWord = passWord;
        this.drivers = drivers;
    }
}
