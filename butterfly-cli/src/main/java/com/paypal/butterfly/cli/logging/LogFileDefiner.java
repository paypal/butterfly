package com.paypal.butterfly.cli.logging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * Defines log file for the application. 
 * Used as plugin in log4j2 configuration file to load
 * the log file during initialization.
 *
 */

@Plugin(name="LogFileDefiner", category=StrLookup.CATEGORY)
public class LogFileDefiner implements StrLookup{

    private static final String LOG_FILE_NAME_SYNTAX = "%s_%s.log";
    private static final String DEBUG_LOG_FILE_NAME_SYNTAX = "%s_%s_debug.log";
    private static final String DEFAULT_LOG_FILE_NAME = String.format(LOG_FILE_NAME_SYNTAX, "butterfly", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

    private static String logFileName = DEFAULT_LOG_FILE_NAME;
    private static boolean customLogFileNameSet = false;
    private static File butterflyHome;
    private static File logFile;

    public static void setButterflyHome(File butterflyHome) {
        LogFileDefiner.butterflyHome = butterflyHome;
    }

    public static void setLogFileName(File applicationFolder, boolean debug) {
        if (!customLogFileNameSet && applicationFolder != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            logFileName = String.format((debug ? DEBUG_LOG_FILE_NAME_SYNTAX : LOG_FILE_NAME_SYNTAX), applicationFolder.getName(), simpleDateFormat.format(new Date()));
            customLogFileNameSet = true;
        }
    }

    private static void setLogFile() {
        logFile = new File(butterflyHome, "logs" + File.separator + logFileName);
    }

    public static File getLogFile() {
        if (logFile == null) {
            setLogFile();
        }
        return logFile;
    }

    @Override
    public String lookup(LogEvent event, String key){
        return LogFileDefiner.getLogFile().getAbsolutePath();
    }

    @Override
    public String lookup(String key){
        return LogFileDefiner.getLogFile().getAbsolutePath();
    }
}
