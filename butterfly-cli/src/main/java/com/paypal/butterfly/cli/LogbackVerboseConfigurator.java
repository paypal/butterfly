package com.paypal.butterfly.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
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
public class LogbackVerboseConfigurator implements VerboseConfigurator {

    private Logger butterflyLogger;

    public LogbackVerboseConfigurator() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        butterflyLogger = loggerContext.getLogger("com.paypal.butterfly");
    }

    @Override
    public void verboseMode(boolean on) {
        if(on) {
            butterflyLogger.setLevel(Level.DEBUG);
        } else {
            butterflyLogger.setLevel(Level.ERROR);
        }
    }

}
