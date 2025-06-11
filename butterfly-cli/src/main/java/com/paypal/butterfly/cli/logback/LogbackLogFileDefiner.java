package com.paypal.butterfly.cli.logback;

import com.paypal.butterfly.cli.logging.LogFileDefinition;

import java.io.File;

public class LogbackLogFileDefiner implements LogFileDefinition {

    private static final LogbackLogFileDefiner INSTANCE = new LogbackLogFileDefiner();

    public static LogbackLogFileDefiner getInstance() {
        return INSTANCE;
    }

    @Override
    public void setButterflyHome(final File butterflyHome) {
        // no op. File logging not supported
    }

    @Override
    public void setLogFileName(final File applicationFolder, final boolean debug) {
        // no op. File logging not supported
    }

    @Override
    public File getLogFile() {
        // File logging not supported
        return null;
    }
}
