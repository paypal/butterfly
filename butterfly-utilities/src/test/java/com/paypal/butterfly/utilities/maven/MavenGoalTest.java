package com.paypal.butterfly.utilities.maven;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mavenGoalSetsRequstObject() {
        String[] goals = {"a", "b", "c"};
        mavenGoal.setGoals(goals);
        mavenGoal.setFailAtEnd();
        mavenGoal.execution(null, null);
        verify(request, times(1)).setFailureBehavior(InvocationRequest.REACTOR_FAIL_AT_END);
        verify(request, times(1)).setPomFile(absoluteFile);
        verify(request, times(1)).setGoals(Arrays.asList(goals));
    }

    @Test
    public void mavenGoalProvidesADescription() {
        String[] goals = {"x", "y", "z"};
        MavenGoal mGoal = new MavenGoal(goals);
        Assert.assertEquals(mGoal.getDescription(), "Execute Maven goal [x, y, z] against pom file ");
    }
}
