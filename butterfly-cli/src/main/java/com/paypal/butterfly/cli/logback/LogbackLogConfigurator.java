package com.paypal.butterfly.cli.logback;

import ch.qos.logback.classic.Logger;
import com.paypal.butterfly.cli.logging.LogConfigurator;
import org.slf4j.event.Level;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@ConditionalOnProperty(name = "butterfly.logging.system", havingValue = "logback")
public class LogbackLogConfigurator extends LogConfigurator {

    @Override
    public void setLoggerLevel(final String logger, final Level level) {
        final ch.qos.logback.classic.Level logbackLevel = ch.qos.logback.classic.Level.toLevel(level.toString());
        getLogbackLogger(logger).setLevel(logbackLevel);
    }

    @Override
    public void setLoggerLevel(final Class logger, final Level level) {
        setLoggerLevel(logger.getName(), level);
    }

    @Override
    public void setVerboseMode(final boolean on) {
        setLoggerLevel("ROOT", Level.DEBUG);
    }

    @Override
    public void setLogToFile(final boolean on) {
        // no op
    }

    @Override
    public boolean isVerboseMode() {
        return getLogbackLogger("ROOT").isDebugEnabled();
    }

    private static Logger getLogbackLogger(final String loggerName) {
        return (Logger) getLogger(loggerName);
    }
}
