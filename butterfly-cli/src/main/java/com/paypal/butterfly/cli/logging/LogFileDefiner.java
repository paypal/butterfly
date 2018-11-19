package com.paypal.butterfly.cli.logging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.paypal.butterfly.cli.ButterflyCliApp;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.PropertyDefiner;
import ch.qos.logback.core.status.Status;
import org.springframework.stereotype.Component;

/**
 * Application name Logback property definer
 *
 * @author facarvalho
 */
public class LogFileDefiner implements PropertyDefiner {

    private static final String LOG_FILE_NAME_SYNTAX = "%s_%s.log";
    private static final String DEBUG_LOG_FILE_NAME_SYNTAX = "%s_%s_debug.log";

    private final String DEFAULT_LOG_FILE_NAME = String.format(LOG_FILE_NAME_SYNTAX, "butterfly", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

    private String logFileName = DEFAULT_LOG_FILE_NAME;
    private boolean customLogFileNameSet = false;
    private File butterflyHome;
    private File logFile;

    public void setButterflyHome(File butterflyHome) {
        this.butterflyHome = butterflyHome;
    }

    public void setLogFileName(File applicationFolder, boolean debug) {
        if (!customLogFileNameSet && applicationFolder != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            logFileName = String.format((debug ? DEBUG_LOG_FILE_NAME_SYNTAX : LOG_FILE_NAME_SYNTAX), applicationFolder.getName(), simpleDateFormat.format(new Date()));
            customLogFileNameSet = true;
        }
    }

    private void setLogFile() {
        logFile = new File(butterflyHome, "logs" + File.separator + logFileName);
    }

    public File getLogFile() {
        if (logFile == null) {
            setLogFile();
        }
        return logFile;
    }

    @Override
    public String getPropertyValue() {
        return getLogFile().getAbsolutePath();
    }

    @Override
    public void setContext(Context context) {
        // Nothing to be done here
    }
    @Override
    public Context getContext() {
        // Nothing to be done here
        return null;
    }
    @Override
    public void addStatus(Status status) {
        // Nothing to be done here
    }
    @Override
    public void addInfo(String s) {
        // Nothing to be done here
    }
    @Override
    public void addInfo(String s, Throwable throwable) {
        // Nothing to be done here
    }
    @Override
    public void addWarn(String s) {
        // Nothing to be done here
    }
    @Override
    public void addWarn(String s, Throwable throwable) {
        // Nothing to be done here
    }

    @Override
    public void addError(String s) {
        // Nothing to be done here
    }
    @Override
    public void addError(String s, Throwable throwable) {
        // Nothing to be done here
    }

}