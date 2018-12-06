package com.paypal.butterfly.cli.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.OutputStreamAppender;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logback specific logging configurator
 *
 * @author facarvalho
 */
@Component
public class LogbackLogConfigurator extends LogConfigurator {

    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    private boolean verboseMode = false;

    @Override
    public void setLoggerLevel(String logger, org.slf4j.event.Level level) {
        if(level == null) {
            throw new IllegalArgumentException("level argument cannot be null");
        }
        loggerContext.getLogger(logger).setLevel(getLogbackLogLevel(level));
    }

    @Override
    public void setLoggerLevel(Class logger, org.slf4j.event.Level level) {
        if(level == null) {
            throw new IllegalArgumentException("level argument cannot be null");
        }
        loggerContext.getLogger(logger).setLevel(getLogbackLogLevel(level));
    }

    private Level getLogbackLogLevel(org.slf4j.event.Level slf4jLevel) {
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
            PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
            patternLayoutEncoder.setPattern("[%d{HH:mm:ss.SSS}] [%highlight(%level)] %msg%n");
            patternLayoutEncoder.setContext(loggerContext);
            patternLayoutEncoder.start();

            Appender<ILoggingEvent> consoleAppender = new ConsoleAppender();
            ((OutputStreamAppender) consoleAppender).setEncoder(patternLayoutEncoder);
            consoleAppender.setContext(loggerContext);
            consoleAppender.start();

            loggerContext.getLogger("com.paypal.butterfly.cli").detachAppender("CONSOLE");
            loggerContext.getLogger("ROOT").addAppender(consoleAppender);
//        } else {
            // TODO
        }
    }

    @Override
    public void setLogToFile(boolean on) {
        loggerContext.getLogger("ROOT").detachAppender("FILE");
    }

    @Override
    public boolean isVerboseMode() {
        return verboseMode;
    }

}
