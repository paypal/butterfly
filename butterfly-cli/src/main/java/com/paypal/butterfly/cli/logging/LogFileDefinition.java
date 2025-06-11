package com.paypal.butterfly.cli.logging;

import com.paypal.butterfly.cli.logback.LogbackLogFileDefiner;

import java.io.File;

public interface LogFileDefinition {

    static LogFileDefinition getInstance() {
        if ("log4j2".equals(System.getProperty("butterfly.logging.system", "log4j2"))) {
            return new LogFileDefinition() {
                @Override
                public void setButterflyHome(final File butterflyHome) {
                    LogFileDefiner.setButterflyHome(butterflyHome);
                }

                @Override
                public void setLogFileName(final File applicationFolder, final boolean debug) {
                    LogFileDefiner.setLogFileName(applicationFolder, debug);
                }

                @Override
                public File getLogFile() {
                    return LogFileDefiner.getLogFile();
                }
            };
        }

        return LogbackLogFileDefiner.getInstance();
    }

    void setButterflyHome(File butterflyHome);

    void setLogFileName(File applicationFolder, boolean debug);

    File getLogFile();
}
