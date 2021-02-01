package com.bassis.tools.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import com.bassis.tools.properties.FileProperties;
import com.bassis.tools.string.StringUtils;


/**
 * 动态配置参数的监听器
 */
public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    /**
     * 初始化读取器
     */
    static FileProperties fileProperties = FileProperties.getInstance();
    private static final String DEFAULT_PROJECT_NAME = "bassis";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_LEVEL = "DEBUG";
    private static final String DEFAULT_DEFAULT_LOGS_HOME = System.getProperty("user.dir");
    private static final String ROOT_PATH = "/application.properties";
    private boolean started = false;

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext loggerContext) {

    }

    @Override
    public void onReset(LoggerContext loggerContext) {

    }

    @Override
    public void onStop(LoggerContext loggerContext) {

    }

    @Override
    public void onLevelChange(Logger logger, Level level) {

    }

    @Override
    public void start() {
        if (started) return;
        fileProperties.read(this.getClass().getSuperclass().getResource(ROOT_PATH));
        String logsHome = fileProperties.getProperty("bassis.logs.home");
        String charset = fileProperties.getProperty("bassis.logs.charset");
        String level = fileProperties.getProperty("bassis.logs.level");
        String projectName = fileProperties.getProperty("bassis.logs.project.name");
        String stdoutPattern = fileProperties.getProperty("bassis.logs.stdout.pattern");
        String errorPattern = fileProperties.getProperty("bassis.logs.error.pattern");
        String infoPattern = fileProperties.getProperty("bassis.logs.info.pattern");
        String debugPattern = fileProperties.getProperty("bassis.logs.debug.pattern");
        String allPattern = fileProperties.getProperty("bassis.logs.all.pattern");
        Context context = getContext();

        if (StringUtils.isEmptyString(logsHome)) logsHome = DEFAULT_DEFAULT_LOGS_HOME;
        context.putProperty("LOGS_DIR", logsHome);

        if (StringUtils.isEmptyString(projectName)) projectName = DEFAULT_PROJECT_NAME;
        context.putProperty("PROJECT", projectName);

        if (StringUtils.isEmptyString(charset)) charset = DEFAULT_CHARSET;
        context.putProperty("LOG_CHARSET", charset);

        if (StringUtils.isEmptyString(level)) level = DEFAULT_LEVEL;
        context.putProperty("LOG_LEVEL", level);

        if (!StringUtils.isEmptyString(stdoutPattern)) context.putProperty("STDOUT_PATTERN", stdoutPattern);
        if (!StringUtils.isEmptyString(errorPattern)) context.putProperty("ERROR_OUT_PATTERN", errorPattern);
        if (!StringUtils.isEmptyString(infoPattern)) context.putProperty("INFO_OUT_PATTERN", infoPattern);
        if (!StringUtils.isEmptyString(debugPattern)) context.putProperty("DEBUG_OUT_PATTERN", debugPattern);
        if (!StringUtils.isEmptyString(allPattern)) context.putProperty("ALL_OUT_PATTERN", allPattern);

        started = true;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
