package com.paypal.butterfly.utilities.maven;


import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author mcrockett
 */
public class MultipleOutputHandlerTest {

    private MultipleOutputHandler multiHandler;

    @Mock
    private MavenInvocationOutputHandler mockHandler;

    private abstract class BogusMavenInvocationOutputHandler implements MavenInvocationOutputHandler<BogusMavenInvocationOutputHandler, Object> {
    }

    @BeforeClass
    public void classSetup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void methodSetup() {
        multiHandler = new MultipleOutputHandler();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void throwsIfExecutionWasNeverStarted() {
        multiHandler.getResult();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void throwsIfExecutionStartedAndAnotherHandlerIsRegistered() {
        multiHandler.consumeLine("hello");
        multiHandler.register(null);
    }

    @Test
    public void callsAllRegisteredHandlersWithLine() {
        BogusMavenInvocationOutputHandler handler0 = Mockito.mock(BogusMavenInvocationOutputHandler.class);
        BogusMavenInvocationOutputHandler handler1 = Mockito.mock(BogusMavenInvocationOutputHandler.class);
        multiHandler.register(handler0);
        multiHandler.register(handler1);
        multiHandler.consumeLine("hello");
        multiHandler.consumeLine("goodbye");
        verify(handler0, times(1)).consumeLine("hello");
        verify(handler1, times(1)).consumeLine("hello");
        verify(handler0, times(1)).consumeLine("goodbye");
        verify(handler1, times(1)).consumeLine("goodbye");
    }

    @Test
    public void doesntAllowAHandlerExceptionToStopConsuming() {
        BogusMavenInvocationOutputHandler handler = Mockito.mock(BogusMavenInvocationOutputHandler.class);
        BogusMavenInvocationOutputHandler badHandler = Mockito.mock(BogusMavenInvocationOutputHandler.class);
        Mockito.doThrow(new IllegalStateException("barf test failure")).when(badHandler).consumeLine(anyString());
        multiHandler.register(handler);
        multiHandler.register(badHandler);
        multiHandler.consumeLine("lineA");
        multiHandler.consumeLine("LineB");
        verify(handler, times(2)).consumeLine(anyString());
    }

    @Test
    public void returnsExceptionInResultsIfHandlerThrows() {
        BogusMavenInvocationOutputHandler badHandler = Mockito.mock(BogusMavenInvocationOutputHandler.class);
        Mockito.doThrow(new IllegalStateException("barf test failure")).when(badHandler).consumeLine(anyString());
        multiHandler.register(badHandler);
        multiHandler.consumeLine("lineA");
        Map<Class<? extends MavenInvocationOutputHandler>, Object> results = multiHandler.getResult();
        Exception e = (Exception)results.get(badHandler.getClass());
        Assert.assertNotNull(e);
        Assert.assertEquals(IllegalStateException.class, e.getClass());
        Assert.assertEquals("barf test failure", e.getMessage());
    }

    @Test
    public void stopsCallingHandlerIfItThrows() {
        BogusMavenInvocationOutputHandler badHandler = Mockito.mock(BogusMavenInvocationOutputHandler.class);
        Mockito.doThrow(new IllegalStateException("barf test failure")).when(badHandler).consumeLine(anyString());
        multiHandler.register(badHandler);
        multiHandler.consumeLine("lineA");
        multiHandler.consumeLine("LineB");
        verify(badHandler, times(1)).consumeLine(anyString());
    }

    @Test
    public void returnsResultsFromEveryHandler() {
        BogusMavenInvocationOutputHandler handler1 = Mockito.mock(BogusMavenInvocationOutputHandler.class);
        Mockito.doReturn("data0").when(mockHandler).getResult();
        Mockito.doReturn("data1").when(handler1).getResult();
        multiHandler.register(mockHandler);
        multiHandler.register(handler1);
        multiHandler.consumeLine("line1");
        multiHandler.consumeLine("line2");
        Map<Class<? extends MavenInvocationOutputHandler>, Object> results = multiHandler.getResult();

        Assert.assertEquals(2, results.size());

        Assert.assertEquals(results.get(mockHandler.getClass()), "data0");
        Assert.assertEquals(results.get(handler1.getClass()), "data1");
    }
}
