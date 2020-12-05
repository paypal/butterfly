package com.paypal.butterfly.cli.logging;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 * Tests for asynchronous logger.
 * All the test cases use log4j2-test.xml configuration file.
 * This configuration file defines the loggers as synchronous.
 * The loggers in production configuration are asynchronous.
 * These tests checks whether asynchronous logging is working
 * as expected.
 *
 * @author badalsarkar
 */

public class LoggerTest{

    private final String testLine ="Butterfly application transformation tool asynchronous logger test";

    @BeforeClass
    public void defineLog4j2ConfigurationFile(){
        // Use the production log4j2 configuration for this test class
        System.setProperty("log4j.configurationFile","log4j2.xml");
    }

    @Test
    public void testLoggingToConsole() throws IOException, InterruptedException{
        PrintStream originalStdOut= System.out;
        ByteArrayOutputStream consoleContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(consoleContent);
        System.setOut(printStream);

        Logger consoleLogger = LoggerFactory.getLogger("com.paypal.butterfly.cli");
        Configuration configuration=LoggerContext.getContext(false).getConfiguration();
        LoggerConfig loggerConfig= configuration.getLoggerConfig(consoleLogger.getName());
        Map<String, Appender> appenderRefs = loggerConfig.getAppenders();

        consoleLogger.info(testLine);
        Thread.sleep(5000);
        System.setOut(originalStdOut);
        printStream.close();
       
        Assert.assertEquals(configuration.getName(),"Production");
        Assert.assertEquals(appenderRefs.size(),1);
        Assert.assertEquals(appenderRefs.get("CONSOLE").toString(), "CONSOLE");
        Assert.assertEquals(loggerConfig.getLevel().toString(),"INFO");
        Assert.assertEquals(consoleContent.toString(), testLine+"\n");
    }

    @Test
    public void testLoggingToFile() throws IOException {
        Logger rootLogger = LoggerFactory.getLogger("root");
        Configuration configuration = LoggerContext.getContext(false).getConfiguration();
        LoggerConfig loggerConfig= configuration.getLoggerConfig(rootLogger.getName());
        Map<String, Appender> appenderRefs = loggerConfig.getAppenders();
        
        rootLogger.error(testLine);
        File logFile = LogFileDefiner.getLogFile();

        Assert.assertEquals(configuration.getName(),"Production");
        Assert.assertEquals(appenderRefs.size(),1);
        Assert.assertEquals(appenderRefs.get("FILE").toString(), "FILE");
        Assert.assertEquals(loggerConfig.getLevel().toString(),"ERROR");
        Assert.assertTrue(logFile.exists());
        Assert.assertTrue(FileUtils.readFileToString(logFile, "UTF-8").contains(testLine));
    }
}



