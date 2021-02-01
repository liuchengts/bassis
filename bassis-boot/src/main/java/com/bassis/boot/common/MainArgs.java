package com.bassis.boot.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainArgs {
    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(MainArgs.class);

    private static class LazyHolder {
        private static final MainArgs INSTANCE = new MainArgs();
    }

    private MainArgs() {
    }

    public static final MainArgs getInstance() {
        return MainArgs.LazyHolder.INSTANCE;
    }

    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
