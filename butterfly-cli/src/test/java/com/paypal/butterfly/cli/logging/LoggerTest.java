package com.paypal.butterfly.cli.logging;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Tests for logging
 *
 * @author badalsarkar
 */

public class LoggerTest{

    @Test
    public void testLoggingToConsoleWithLevelInfo() throws IOException{
        PrintStream originalStdOut= System.out;
        ByteArrayOutputStream consoleContent = new ByteArrayOutputStream();
        PrintStream printToFile = new PrintStream(consoleContent);
        System.setOut(printToFile);
        LoggerFactory.getLogger("com.paypal.butterfly.cli").info("Butterfly application transformation tool");
        System.setOut(originalStdOut);
        printToFile.close();
        Assert.assertTrue(consoleContent.toString().compareTo("Butterfly application transformation tool\n")==0);
    }

    @Test
    public void testLoggingToFile() throws IOException {
        String fileName = System.getProperty("user.dir") +"/logs/testLoggingToFile.txt";
        String testLine ="Butterfly application transformation tool";
        System.setProperty("logFile",fileName);
        LoggerFactory.getLogger("root").error(testLine);
        File logFile = new File(fileName);
        Assert.assertTrue(logFile.exists());
        Assert.assertTrue(FileUtils.readFileToString(logFile, "UTF-8").contains("Butterfly application transformation tool"));
        logFile.delete();
    }
}



