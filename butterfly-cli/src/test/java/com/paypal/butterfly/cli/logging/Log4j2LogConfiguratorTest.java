package com.paypal.butterfly.cli.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.LoggerContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Log4j2VerboseConfiguratorTest
 *
*/
public class Log4j2LogConfiguratorTest extends PowerMockTestCase {

    @InjectMocks
    private Log4j2LogConfigurator log4j2VerboseConfigurator;

    @Mock
    private static final Log4jLoggerFactory log4jLoggerFactory = (Log4jLoggerFactory) LoggerFactory.getILoggerFactory();

    @Test
    public void testVerboseOn() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setDebugMode(true);
        Assert.assertTrue(LogManager.getLogger("com.paypal.butterfly").getLevel() == org.apache.logging.log4j.Level.DEBUG);
    }

    @Test
    public void testVerboseOff() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setDebugMode(false);
        Assert.assertTrue(LogManager.getLogger("com.paypal.butterfly").getLevel() == org.apache.logging.log4j.Level.INFO);
    }

    @Test
    public void testVerboseOnAndRootLoggerHasConsoleAppender(){
        Configuration config = ((LoggerContext)LogManager.getContext(false)).getConfiguration();
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setVerboseMode(true);
        LogManager.getLogger("com.paypal.butterfly.cli");
        Assert.assertTrue(config.getLoggerConfig("com.paypal.butterfly.cli").getAppenders().isEmpty());
        Assert.assertTrue(config.getRootLogger().getAppenders().get("DefaultConsole-2").getName().compareTo("DefaultConsole-2") == 0);
        Assert.assertTrue(config.getRootLogger().getAppenders().get("Routing").getName().compareTo("Routing") == 0);
    }

    @Test
    public void testLoggerAsStringAndLogLevelAsInfo() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.INFO);
        Assert.assertTrue(LogManager.getLogger("com.paypal.butterfly.cli").getLevel() == org.apache.logging.log4j.Level.INFO);
    }

    
    @Test
    public void testChildLogLevelIsInheritedFromParent(){
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.INFO);
        Assert.assertTrue(LogManager.getLogger("com.paypal.butterfly.cli.test").getLevel() == org.apache.logging.log4j.Level.INFO);
    }

    @Test
    public void testLoggerAsStringAndLogLevelAsDebug() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.DEBUG);
        Assert.assertTrue(LogManager.getLogger("com.paypal.butterfly.cli").getLevel() == org.apache.logging.log4j.Level.DEBUG);
    }

    @Test
    public void testLoggerAsStringAndLogLevelAsWarn() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.WARN);
        Assert.assertTrue(LogManager.getLogger("com.paypal.butterfly.cli").getLevel() == org.apache.logging.log4j.Level.WARN);
    }

    @Test
    public void testLoggerAsStringAndLogLevelAsError() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.ERROR);
        Assert.assertTrue(LogManager.getLogger("com.paypal.butterfly.cli").getLevel() == org.apache.logging.log4j.Level.ERROR);
    }

    @Test
    public void testLoggerAsStringAndLogLevelAsInvalid() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        try {
            log4j2VerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.TRACE);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("Unknown log level"));
        }
    }

    @Test
    public void testLoggerAsStringAndLogLevelAsNull() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        try {
            log4j2VerboseConfigurator.setLoggerLevel("", null);
            Assert.assertFalse(false);
        }catch(IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("level argument cannot be null"));
        }
    }

    @Test
    public void testLoggerAsEmptyStringAndValidLogLevel(){
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        try {
            log4j2VerboseConfigurator.setLoggerLevel("", Level.WARN);
            Assert.assertFalse(false);
        }catch(IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("name argument cannot be null"));
        }
    }

    @Test
    public void testLoggerAsStringAndLogLevelAsWarnAndLoggerAsNull() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        try {
            log4j2VerboseConfigurator.setLoggerLevel((String)null, Level.WARN);
            Assert.assertFalse(false);
        }catch(IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("name argument cannot be null"));
        }
    }


    @Test
    public void testLoggerAsClassAndLogLevelAsInfo() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel(this.getClass(), Level.INFO);
        Assert.assertTrue(LogManager.getLogger(this.getClass()).getLevel() == org.apache.logging.log4j.Level.INFO);
    }


    @Test
    public void testLoggerAsClassAndLogLevelAsDebug() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel(this.getClass(), Level.DEBUG);
        Assert.assertTrue(LogManager.getLogger(this.getClass()).getLevel() == org.apache.logging.log4j.Level.DEBUG);
    }

    @Test
    public void testLoggerAsClassAndLevelAsWarn() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel(this.getClass(), Level.WARN);
        Assert.assertTrue(LogManager.getLogger(this.getClass()).getLevel() == org.apache.logging.log4j.Level.WARN);
    }

    @Test
    public void testLoggerAsClassAndLogLevelAsError() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        log4j2VerboseConfigurator.setLoggerLevel(this.getClass(), Level.ERROR);
        Assert.assertTrue(LogManager.getLogger(this.getClass()).getLevel() == org.apache.logging.log4j.Level.ERROR);
    }

    @Test
    public void testLoggerAsClassAndLogLevelAsInvalid() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        try {
            log4j2VerboseConfigurator.setLoggerLevel(this.getClass(), Level.TRACE);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("Unknown log level"));
        }
    }

    @Test
    public void testLoggerAsClassAndLogLevelAsNull() {
        Assert.assertNotNull(log4j2VerboseConfigurator);
        Assert.assertNotNull(log4jLoggerFactory);
        try {
            log4j2VerboseConfigurator.setLoggerLevel("", null);
            Assert.assertFalse(false);
        }catch(IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("level argument cannot be null"));
        }
    }
}
