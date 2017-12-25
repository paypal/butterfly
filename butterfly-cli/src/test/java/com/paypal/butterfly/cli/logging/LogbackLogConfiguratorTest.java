package com.paypal.butterfly.cli.logging;

import ch.qos.logback.classic.LoggerContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LogbackVerboseConfiguratorTest
 *
 * @author vkuncham
 */
public class LogbackLogConfiguratorTest extends PowerMockTestCase {

    @InjectMocks
    private LogbackLogConfigurator logbackVerboseConfigurator;

    @Mock
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    @Test
    public void testVerboseOn() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setDebugMode(true);
        Assert.assertTrue(loggerContext.getLogger("com.paypal.butterfly").getLevel() == ch.qos.logback.classic.Level.DEBUG);
    }


    @Test
    public void testVerboseOff() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setDebugMode(false);
        Assert.assertTrue(loggerContext.getLogger("com.paypal.butterfly").getLevel() == ch.qos.logback.classic.Level.INFO);
    }


    @Test
    public void testLoggerAsStringAndLogBackLevelAsInfo() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.INFO);
        Assert.assertTrue(loggerContext.getLogger("com.paypal.butterfly.cli").getLevel() == ch.qos.logback.classic.Level.INFO);
    }

    @Test
    public void testLoggerAsStringAndLogBackLevelAsInfoWithWrongPackage() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.INFO);
        Assert.assertFalse(loggerContext.getLogger("com.paypal.butterfly.cli.test").getLevel() == ch.qos.logback.classic.Level.INFO);
    }

    @Test
    public void testLoggerAsStringAndLogBackLevelAsDebug() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.DEBUG);
        Assert.assertTrue(loggerContext.getLogger("com.paypal.butterfly.cli").getLevel() == ch.qos.logback.classic.Level.DEBUG);
    }

    @Test
    public void testLoggerAsStringAndLogBackLevelAsWarn() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.WARN);
        Assert.assertTrue(loggerContext.getLogger("com.paypal.butterfly.cli").getLevel() == ch.qos.logback.classic.Level.WARN);
    }

    @Test
    public void testLoggerAsStringAndLogBackLevelAsError() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.ERROR);
        Assert.assertTrue(loggerContext.getLogger("com.paypal.butterfly.cli").getLevel() == ch.qos.logback.classic.Level.ERROR);
    }

    @Test
    public void testLoggerAsStringAndLogBackLevelAsInvalid() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        try {
            logbackVerboseConfigurator.setLoggerLevel("com.paypal.butterfly.cli", Level.TRACE);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("Unknown log level"));
        }
    }

    @Test
    public void testLoggerAsStringAndLogBackLevelAsNull() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        try {
            logbackVerboseConfigurator.setLoggerLevel("", null);
            Assert.assertFalse(false);
        }catch(IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("level argument cannot be null"));
        }
    }

    @Test
    public void testLoggerAsStringAndLogBackLevelAndLoggerAsNull() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        try {
            logbackVerboseConfigurator.setLoggerLevel((String)null, Level.WARN);
            Assert.assertFalse(false);
        }catch(IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("name argument cannot be null"));
        }
    }


    @Test
    public void testLoggerAsClassAndLogBackLevelAsInfo() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel(this.getClass(), Level.INFO);
        Assert.assertTrue(loggerContext.getLogger(this.getClass()).getLevel() == ch.qos.logback.classic.Level.INFO);
    }

    @Test
    public void testLoggerAsClassAndLogBackLevelAsInfoWithWrongPackage() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel(this.getClass(), Level.INFO);
        Assert.assertFalse(loggerContext.getLogger(Object.class).getLevel() == ch.qos.logback.classic.Level.INFO);
    }

    @Test
    public void testLoggerAsClassAndLogBackLevelAsDebug() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel(this.getClass(), Level.DEBUG);
        Assert.assertTrue(loggerContext.getLogger(this.getClass()).getLevel() == ch.qos.logback.classic.Level.DEBUG);
    }

    @Test
    public void testLoggerAsClassAndLogBackLevelAsWarn() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel(this.getClass(), Level.WARN);
        Assert.assertTrue(loggerContext.getLogger(this.getClass()).getLevel() == ch.qos.logback.classic.Level.WARN);
    }

    @Test
    public void testLoggerAsClassAndLogBackLevelAsError() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        logbackVerboseConfigurator.setLoggerLevel(this.getClass(), Level.ERROR);
        Assert.assertTrue(loggerContext.getLogger(this.getClass()).getLevel() == ch.qos.logback.classic.Level.ERROR);
    }

    @Test
    public void testLoggerAsClassAndLogBackLevelAsInvalid() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        try {
            logbackVerboseConfigurator.setLoggerLevel(this.getClass(), Level.TRACE);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("Unknown log level"));
        }
    }

    @Test
    public void testLoggerAsClassAndLogBackLevelAsNull() {
        Assert.assertNotNull(logbackVerboseConfigurator);
        Assert.assertNotNull(loggerContext);
        try {
            logbackVerboseConfigurator.setLoggerLevel("", null);
            Assert.assertFalse(false);
        }catch(IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().equals("level argument cannot be null"));
        }
    }
}
