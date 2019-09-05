package com.bassis.boot.application;

import org.apache.log4j.Logger;

public class MainArgsUtil {
    private static final long serialVersionUID = 1L;
    static final Logger logger = Logger.getLogger(MainArgsUtil.class);

    private static class LazyHolder {
        private static final MainArgsUtil INSTANCE = new MainArgsUtil();
    }

    private MainArgsUtil() {
    }

    public static final MainArgsUtil getInstance() {
        return MainArgsUtil.LazyHolder.INSTANCE;
    }

    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
