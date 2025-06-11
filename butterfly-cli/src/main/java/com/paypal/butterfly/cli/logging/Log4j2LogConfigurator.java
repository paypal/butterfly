package com.paypal.butterfly.cli.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.appender.ConsoleAppender;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Log4j2 specific logging configurator
 *
 * @author badalsarkar
 */

@Component
@ConditionalOnProperty(name = "butterfly.logging.system", havingValue = "log4j2", matchIfMissing = true)
public class Log4j2LogConfigurator extends LogConfigurator {

    private static final LoggerContext context = (LoggerContext)LogManager.getContext(false); 
    private static final Configuration config = context.getConfiguration();
    private boolean verboseMode = false;

    @Override
    public void setLoggerLevel(String logger, org.slf4j.event.Level level) {
        if(level == null) {
            throw new IllegalArgumentException("level argument cannot be null");
        }

        if(logger==null || logger.length()==0){
            throw new IllegalArgumentException("name argument cannot be null");
        }
        config.getLoggerConfig(logger).setLevel(getLog4j2LogLevel(level));
        context.updateLoggers();
    }

    @Override
    public void setLoggerLevel(Class logger, org.slf4j.event.Level level) {
        if(level == null) {
            throw new IllegalArgumentException("level argument cannot be null");
        }
        config.getLoggerConfig(LogManager.getLogger(logger).getName()).setLevel(getLog4j2LogLevel(level));
        context.updateLoggers();
    }

    private Level getLog4j2LogLevel(org.slf4j.event.Level slf4jLevel) {
        if(slf4jLevel.equals(org.slf4j.event.Level.INFO)) return Level.INFO;
        if(slf4jLevel.equals(org.slf4j.event.Level.DEBUG)) return Level.DEBUG;
        if(slf4jLevel.equals(org.slf4j.event.Level.WARN)) return Level.WARN;
        if(slf4jLevel.equals(org.slf4j.event.Level.ERROR)) return Level.ERROR;

        throw new IllegalArgumentException("Unknown log level");
    }

    @Override
    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
        if (verboseMode) {
            Layout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern("[%d{HH:mm:ss.SSS}] [%highlight{%level}] %msg%n").build();
            Appender consoleAppender = ConsoleAppender.createDefaultAppenderForLayout(layout);
            consoleAppender.start();
            config.getLoggerConfig("com.paypal.butterfly.cli").removeAppender("CONSOLE");
            config.getRootLogger().addAppender(consoleAppender,null,null);
            context.updateLoggers();
        }
    }

    @Override
    public void setLogToFile(boolean on) {
        config.getRootLogger().removeAppender("FILE");
    }

    @Override
    public boolean isVerboseMode() {
        return verboseMode;
    }
}
