package com.bassis.tools.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义异常类
 */
public class CustomException extends Exception {

    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(CustomException.class);

    private CustomException() {
    }

    public static Object parsing(String message, Throwable cause, Object obj) {
        logger.error(message, cause);
        return obj;
    }

    public static void throwOut(String message) {
        logger.error(message);
        throw new RuntimeException(message);
    }

    public static void throwOut(String message, Throwable cause) {
        logger.error(message, cause);
        throw new RuntimeException(message, cause);
    }

    public static void throwOut(Throwable cause) {
        logger.error(cause.getMessage(), cause);
        throw new RuntimeException(cause);
    }
}
