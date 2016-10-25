package com.paypal.butterfly.utilities.maven;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;

/**
 * @author mcrockett
 */
public class MavenGoalTest {
    @Mock
    InvocationRequest request = new DefaultInvocationRequest();

    @Mock
    File absoluteFile = new File("absolute_path");

    @InjectMocks
    MavenGoal mavenGoal = new MavenGoal();
    
    @BeforeClass
    public void classSetup() throws Exception {
        String[] goals = {"a", "b", "c"};
        MockitoAnnotations.initMocks(this);
        mavenGoal.setGoals(goals);
    }

    @Test
    public void setsRequestObject() {
        Mockito.reset(request);
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
}
