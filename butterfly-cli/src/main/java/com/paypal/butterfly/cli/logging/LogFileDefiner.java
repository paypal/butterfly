package com.paypal.butterfly.cli.logging;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.PropertyDefiner;
import ch.qos.logback.core.status.Status;
import com.paypal.butterfly.cli.ButterflyCliApp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Application name Logback property definer
 *
 * @author facarvalho
 */
public class LogFileDefiner implements PropertyDefiner {

    private static final String LOG_FILE_NAME_SYNTAX = "%s_%s.log";
    private static final String DEBUG_LOG_FILE_NAME_SYNTAX = "%s_%s_debug.log";
    private static final String DEFAULT_LOG_FILE_NAME = String.format(LOG_FILE_NAME_SYNTAX, "butterfly", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

    private static String logFileName = DEFAULT_LOG_FILE_NAME;
    private static boolean customLogFileNameSet = false;
    private static File logFile;

    public static void setLogFileName(File applicationFolder, boolean debug) {
        if (!customLogFileNameSet && applicationFolder != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            logFileName = String.format((debug ? DEBUG_LOG_FILE_NAME_SYNTAX : LOG_FILE_NAME_SYNTAX), applicationFolder.getName(), simpleDateFormat.format(new Date()));
            customLogFileNameSet = true;
        }
    }

    private static void setLogFile() {
        logFile = new File(ButterflyCliApp.getButterflyHome(), "logs" + File.separator + logFileName);
    }

    public static File getLogFile() {
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
    public void setContext(Context context) {}
    @Override
    public Context getContext() {
        return null;
    }
    @Override
    public void addStatus(Status status) {}
    @Override
    public void addInfo(String s) {}
    @Override
    public void addInfo(String s, Throwable throwable) {}
    @Override
    public void addWarn(String s) {}
    @Override
    public void addWarn(String s, Throwable throwable) {}
    @Override
    public void addError(String s) {}
    @Override
    public void addError(String s, Throwable throwable) {}

}