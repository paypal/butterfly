package com.paypal.butterfly.cli.logging;

import org.slf4j.event.Level;

/**
 * Allows Butterfly to configure logging in
 * runtime directly in the SLF4J implementation.
 * Ideally we should be 100% independent of the
 * SLF4J implementation, but the API doesn't allow
 * changing the log level in runtime, so here we
 * are doing it behind the scenes.
 * <br>
 * At least we are providing this interface
 * to isolate these concerns, which will make it
 * easier to maintain this code if we ever replace
 * SLF4J implementations
 *
 * @author facarvalho
 */
public abstract class LogConfigurator {

    public LogConfigurator() {
        setLoggerLevel("com.paypal.butterfly", Level.INFO);
    }

    public abstract void setLoggerLevel(String logger, Level level);

    public abstract void setLoggerLevel(Class logger, Level level);

    public void setDebugMode(boolean on) {
        if(on) {
            setLoggerLevel("com.paypal.butterfly", Level.DEBUG);
        } else {
            setLoggerLevel("com.paypal.butterfly", Level.INFO);
        }
    }

    public abstract void setVerboseMode(boolean on);

    public abstract void setLogToFile(boolean on);

    public abstract boolean isVerboseMode();

}
