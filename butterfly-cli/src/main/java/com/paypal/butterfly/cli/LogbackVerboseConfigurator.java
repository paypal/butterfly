package com.paypal.butterfly.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logback specific class that allows changing Butterfly
 * logger level to DEBUG (which is the way to run it in
 * verbose mode)
 *
 * @author facarvalho
 */
@Component
public class LogbackVerboseConfigurator extends VerboseConfigurator {

    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    @Override
    void setLoggerLevel(String logger, org.slf4j.event.Level level) {
        loggerContext.getLogger(logger).setLevel(getLogbackLogLevel(level));
    }

    @Override
    void setLoggerLevel(Class logger, org.slf4j.event.Level level) {
        loggerContext.getLogger(logger).setLevel(getLogbackLogLevel(level));
    }

    private Level getLogbackLogLevel(org.slf4j.event.Level slf4jLevel) {
        if(slf4jLevel.equals(org.slf4j.event.Level.INFO)) return Level.INFO;
        if(slf4jLevel.equals(org.slf4j.event.Level.DEBUG)) return Level.DEBUG;
        if(slf4jLevel.equals(org.slf4j.event.Level.WARN)) return Level.WARN;
        if(slf4jLevel.equals(org.slf4j.event.Level.ERROR)) return Level.ERROR;

        throw new IllegalArgumentException("Unknown log level");
    }

}
