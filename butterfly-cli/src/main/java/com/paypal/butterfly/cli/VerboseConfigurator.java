package com.paypal.butterfly.cli;

import org.slf4j.event.Level;

/**
 * Allows Butterfly to run in verbose mode by
 * setting the log level directly in the SLF4J
 * implementation. Ideally we should be 100%
 * independent of the SLF4J implementation, but
 * the API doesn't allow changing the log level in
 * runtime, so here we are doing it behind the
 * scenes. At least we are providing this interface
 * to isolate these concerns, which will make it
 * easier to maintain this code if we ever replace
 * SLF4J implementations
 *
 * @author facarvalho
 */
public abstract class VerboseConfigurator {

    VerboseConfigurator() {
        setLoggerLevel(ButterflyCliApp.class, Level.INFO);
    }

    abstract void setLoggerLevel(String logger, Level level);

    abstract void setLoggerLevel(Class logger, Level level);

    void verboseMode(boolean on) {
        if(on) {
            setLoggerLevel("com.paypal.butterfly", Level.DEBUG);
            setLoggerLevel(ButterflyCliApp.class, Level.DEBUG);
        } else {
            setLoggerLevel("com.paypal.butterfly", Level.ERROR);
            setLoggerLevel(ButterflyCliApp.class, Level.INFO);
        }
    }

}
