package com.paypal.butterfly.utilities.maven;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author mcrockett
 */
public class MavenGoalTest {

    @Mock
    private InvocationRequest request = new DefaultInvocationRequest();

    @Mock
    private Invoker invoker = new DefaultInvoker();

    @Mock
    private MultipleOutputHandler multipleOutputHandler = new MultipleOutputHandler();

    @Mock
    private File absoluteFile = new File("absolute_path");

    @InjectMocks
    private MavenGoal mavenGoal = new MavenGoal();
    
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        String[] goals = {"a", "b", "c"};
        mavenGoal.setGoals(goals);
    }

    @Test
    public void setsRequestObjectCorrectly() {
        String[] goals = {"a", "b", "c"};
        mavenGoal.setGoals(goals);
        mavenGoal.setFailAtEnd();
        mavenGoal.execution(null, null);
        verify(request, times(1)).setFailureBehavior(InvocationRequest.REACTOR_FAIL_AT_END);
        verify(request, times(1)).setPomFile(absoluteFile);
        verify(request, times(1)).setGoals(anyList());
    }

    @Test
    public void doesntSetIfFailureBehaviorIsUnset() {
        Mockito.reset(request);
        mavenGoal.execution(null, null);
        verify(request, times(0)).setFailureBehavior(anyString());
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void throwsIfTryAndSetPropertiesToNull() {
        mavenGoal.setProperties(null);
    }

    @Test
    public void doesntSetIfNullProperties() {
        Mockito.reset(request);
        mavenGoal.execution(null, null);
        verify(request, times(0)).setProperties((Properties)any());
    }

    @Test
    public void doesntSetIfEmptyProperties() {
        Mockito.reset(request);
        mavenGoal.setProperties(new Properties());
        mavenGoal.execution(null, null);
        verify(request, times(0)).setProperties((Properties)any());
    }

    @Test
    public void getPropertiesReturnsOriginalObject() {
        Properties p = new Properties();
        p.put("a",  "b");
        mavenGoal.setProperties(p);
        Assert.assertEquals(mavenGoal.getProperties(), p);
    }

    @Test
    public void providesADescription() {
        String[] goals = {"x", "y", "z"};
        MavenGoal mGoal = new MavenGoal(goals);
        Assert.assertEquals(mGoal.getDescription(), "Execute Maven goal [x, y, z] against pom file ");
    }

    @Test
    public void isInvokerExceptionWithNullDataIfNonZeroExitCodeAndHandlerGetResultThrows() throws MavenInvocationException {
        InvocationResult r = Mockito.mock(InvocationResult.class);
        CommandLineException invokerException = new CommandLineException("test error");
        IllegalStateException resultException = new IllegalStateException("exception");
        Mockito.when(multipleOutputHandler.getResult()).thenThrow(resultException);
        Mockito.when(r.getExitCode()).thenReturn(1);
        Mockito.when(r.getExecutionException()).thenReturn(invokerException);
        Mockito.when(invoker.execute(request)).thenReturn(r);
        mavenGoal.setOutputHandlers(new GenericErrorsOutputHandler());
        TUExecutionResult result = mavenGoal.execution(new File("/blah/pom.xml"), null);
        Assert.assertEquals(result.getException(), invokerException);
        Assert.assertEquals(result.getValue(), null);
    }

    @Test
    public void isResultExceptionIfNonZeroExitCodeAndNoInvokerException() throws MavenInvocationException {
        InvocationResult r = Mockito.mock(InvocationResult.class);
        IllegalStateException e = new IllegalStateException("exception");
        Mockito.when(multipleOutputHandler.getResult()).thenThrow(e);
        Mockito.when(r.getExitCode()).thenReturn(1);
        Mockito.when(r.getExecutionException()).thenReturn(null);
        Mockito.when(invoker.execute(request)).thenReturn(r);
        TUExecutionResult result = mavenGoal.execution(new File("/blah/pom.xml"), null);
        Assert.assertEquals(result.getException(), e);
        Assert.assertEquals(result.getValue(), null);
    }

    @Test
    public void isSelfMadeExceptionIfNonZeroExitCodeAndHandlerGetResultIsValidAndNoInvokerException() throws MavenInvocationException {
        InvocationResult r = Mockito.mock(InvocationResult.class);
        Map<Class<? extends MavenInvocationOutputHandler>, Object> value = new HashMap<Class<? extends MavenInvocationOutputHandler>, Object>();
        value.put(new GenericErrorsOutputHandler().getClass(), "Hello!");
        Mockito.when(multipleOutputHandler.getResult()).thenReturn(value);
        Mockito.when(r.getExitCode()).thenReturn(1);
        Mockito.when(r.getExecutionException()).thenReturn(null);
        Mockito.when(invoker.execute(request)).thenReturn(r);
        TUExecutionResult result = mavenGoal.execution(new File("/blah/pom.xml"), null);
        Assert.assertEquals(result.getException().getMessage(), "Maven goals [a, b, c] execution failed with exit code 1");
        Assert.assertEquals(result.getValue(), value);
    }

    @Test
    public void isInvokerExceptionWithResultDataIfNonZeroExitCodeAndHandlerGetResultIsValid() throws MavenInvocationException {
        InvocationResult r = Mockito.mock(InvocationResult.class);
        Map<Class<? extends MavenInvocationOutputHandler>, Object> value = new HashMap<Class<? extends MavenInvocationOutputHandler>, Object>();
        value.put(new GenericErrorsOutputHandler().getClass(), "Hello!");
        CommandLineException invokerException = new CommandLineException("test error");
        Mockito.when(multipleOutputHandler.getResult()).thenReturn(value);
        Mockito.when(r.getExitCode()).thenReturn(1);
        Mockito.when(r.getExecutionException()).thenReturn(invokerException);
        Mockito.when(invoker.execute(request)).thenReturn(r);
        TUExecutionResult result = mavenGoal.execution(new File("/blah/pom.xml"), null);
        Assert.assertEquals(result.getException(), invokerException);
        Assert.assertEquals(result.getValue(), value);
    }

    @Test
    public void isValidResultIfZeroErrorCode() throws MavenInvocationException {
        InvocationResult r = Mockito.mock(InvocationResult.class);
        Map<Class<? extends MavenInvocationOutputHandler>, Object> value = new HashMap<Class<? extends MavenInvocationOutputHandler>, Object>();
        value.put(new GenericErrorsOutputHandler().getClass(), "Hello!");
        Mockito.when(multipleOutputHandler.getResult()).thenReturn(value);
        Mockito.when(r.getExitCode()).thenReturn(0);
        Mockito.when(r.getExecutionException()).thenReturn(null);
        Mockito.when(invoker.execute(request)).thenReturn(r);
        TUExecutionResult result = mavenGoal.execution(new File("/blah/pom.xml"), null);
        Assert.assertEquals(result.getException(), null);
        Assert.assertEquals(result.getValue(), value);
    }
}
