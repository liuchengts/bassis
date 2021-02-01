package com.bassis.boot.common;

//import org.apache.log4j.Logger;

public class MainArgs {
    private static final long serialVersionUID = 1L;
//    static final Logger logger = Logger.getLogger(MainArgs.class);

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
