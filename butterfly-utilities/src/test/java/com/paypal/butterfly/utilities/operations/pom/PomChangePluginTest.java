package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.*;

/**
 * Unit test for {@link PomChangePlugin}
 *
 * @author praveesingh
 */
public class PomChangePluginTest extends TransformationUtilityTestHelper {

    @Test
    public void addVersionTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "build-helper-maven-plugin").relative("pom.xml").setVersion("6.11");

        assertNull(getPluginBeforeChange(uut).getVersion());
        executeAndAssertSuccess(uut);
        assertEquals(getPluginAfterChange(uut).getVersion(), "6.11");
    }

    @Test
    public void changeVersionTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "cobertura-maven-plugin").relative("pom.xml").setVersion("3.5");

        assertEquals(getPluginBeforeChange(uut).getVersion(), "2.7");
        executeAndAssertSuccess(uut);
        assertEquals(getPluginAfterChange(uut).getVersion(), "3.5");
    }

    @Test
    public void removeVersionTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "cobertura-maven-plugin").relative("pom.xml").removeVersion();

        assertEquals(getPluginBeforeChange(uut).getVersion(), "2.7");
        assertTrue(uut.isRemoveVersion());
        executeAndAssertSuccess(uut);
        assertNull(getPluginAfterChange(uut).getVersion());
    }

    @Test
    public void addExtensionTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "cobertura-maven-plugin").relative("pom.xml").setExtensions("true");

        assertNull(getPluginBeforeChange(uut).getExtensions());
        executeAndAssertSuccess(uut);
        assertEquals(getPluginAfterChange(uut).getExtensions(), "true");
    }

    @Test
    public void changeExtensionTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "build-helper-maven-plugin").relative("pom.xml").setExtensions("false");

        assertEquals(getPluginBeforeChange(uut).getExtensions(), "true");
        executeAndAssertSuccess(uut);
        assertEquals(getPluginAfterChange(uut).getExtensions(), "false");
    }

    @Test
    public void removeExtensionTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "build-helper-maven-plugin").relative("pom.xml").removeExtensions();

        assertEquals(getPluginBeforeChange(uut).getExtensions(), "true");
        assertTrue(uut.isRemoveExtensions());
        executeAndAssertSuccess(uut);
        assertNull(getPluginAfterChange(uut).getExtensions());
    }

    @Test
    public void addExecutionTest() throws IOException, XmlPullParserException {
        final PluginExecution pluginExecution = new PluginExecutionBuilder().setId("test").setPhase("build").addGoal("clean install").build();
        List<PluginExecution> executions = new ArrayList<PluginExecution>(){{ add(pluginExecution); }};
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "cobertura-maven-plugin").relative("pom.xml").setExecutions(executions);

        assertTrue(getPluginBeforeChange(uut).getExecutions().isEmpty());
        executeAndAssertSuccess(uut);
        assertEquals(getPluginAfterChange(uut).getExecutions().size(), executions.size());
        assertEquals(getPluginAfterChange(uut).getExecutions().get(0).getId(), pluginExecution.getId());
        assertEquals(getPluginAfterChange(uut).getExecutions().get(0).getGoals(), pluginExecution.getGoals());
        assertEquals(getPluginAfterChange(uut).getExecutions().get(0).getPhase(), pluginExecution.getPhase());
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void addExecutionTestShouldThrowErrorWhenSameIdForMoreThanOneExecution() throws IOException, XmlPullParserException {
        final PluginExecution pluginExecution = new PluginExecutionBuilder().setId("test").setPhase("build").addGoal("clean build").build();
        final PluginExecution pluginExecution2 = new PluginExecutionBuilder().setId("test").setPhase("install").addGoal("clean install").build();
        List<PluginExecution> executions = new ArrayList<PluginExecution>(){{ add(pluginExecution); add(pluginExecution2); }};
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "cobertura-maven-plugin").relative("pom.xml").setExecutions(executions);

        assertTrue(getPluginBeforeChange(uut).getExecutions().isEmpty());
        executeAndAssertSuccess(uut);
        fail();
    }

    @Test
    public void changeExecutionsTest() throws IOException, XmlPullParserException {
        final PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setId("test-execution");
        pluginExecution.setPhase("build");
        pluginExecution.addGoal("clean install");
        List<PluginExecution> executions = new ArrayList<PluginExecution>(){{ add(pluginExecution); }};
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "build-helper-maven-plugin").relative("pom.xml").setExecutions(executions);

        assertFalse(getPluginBeforeChange(uut).getExecutions().isEmpty());
        assertNotEquals(getPluginBeforeChange(uut).getExecutions().size(), executions.size());
        assertFalse(getPluginBeforeChange(uut).getExecutionsAsMap().containsKey(pluginExecution.getId()));

        executeAndAssertSuccess(uut);

        assertFalse(getPluginAfterChange(uut).getExecutions().isEmpty());
        assertNotEquals(getPluginBeforeChange(uut).getExecutions().size(), getPluginAfterChange(uut).getExecutions().size());
        assertEquals(getPluginAfterChange(uut).getExecutions().size(), executions.size());
        assertTrue(getPluginAfterChange(uut).getExecutionsAsMap().containsKey(pluginExecution.getId()));
    }

    @Test
    public void removeExecutionsTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "build-helper-maven-plugin").relative("pom.xml").removeExecutions();

        assertFalse(getPluginBeforeChange(uut).getExecutions().isEmpty());
        assertTrue(uut.isRemoveExecutions());
        executeAndAssertSuccess(uut);
        assertTrue(getPluginAfterChange(uut).getExecutions().isEmpty());
    }

    @Test
    public void addPluginDependencyTest() throws IOException, XmlPullParserException {
        final Dependency dependency = new Dependency();
        dependency.setGroupId("com.test.butterfly");
        dependency.setArtifactId("butterfly-transformation");
        dependency.setVersion("1.0.0-SNAPSHOT");
        List<Dependency> dependencyList = Arrays.asList(dependency);
        PomChangePlugin uut = new PomChangePlugin("org.codehaus.mojo", "cobertura-maven-plugin").relative("pom.xml").setPluginDependencies(dependencyList);

        assertTrue(getPluginBeforeChange(uut).getDependencies().isEmpty());
        executeAndAssertSuccess(uut);
        assertEquals(getPluginAfterChange(uut).getDependencies().size(), dependencyList.size());
        assertEquals(getPluginAfterChange(uut).getDependencies().get(0).getArtifactId(), dependency.getArtifactId());
        assertEquals(getPluginAfterChange(uut).getDependencies().get(0).getGroupId(), dependency.getGroupId());
        assertEquals(getPluginAfterChange(uut).getDependencies().get(0).getVersion(), dependency.getVersion());
    }

    @Test
    public void changePluginDependencyTest() throws IOException, XmlPullParserException {
        final Dependency dependency = new Dependency();
        dependency.setGroupId("com.test.butterfly");
        dependency.setArtifactId("butterfly-transformation");
        dependency.setVersion("1.0.0-SNAPSHOT");
        List<Dependency> dependencyList = Arrays.asList(dependency);
        PomChangePlugin uut = new PomChangePlugin("org.maven.plugins", "maven-antrun-plugin").relative("pom.xml").setPluginDependencies(dependencyList);

        assertFalse(getPluginBeforeChange(uut).getDependencies().isEmpty());
        assertNotEquals(getPluginBeforeChange(uut).getDependencies().size(), dependencyList.size());

        executeAndAssertSuccess(uut);
        List<Dependency> dependenciesAfterPluginChange = getPluginAfterChange(uut).getDependencies();

        assertEquals(dependenciesAfterPluginChange.size(), dependencyList.size());
        assertEquals(dependenciesAfterPluginChange.get(0).getArtifactId(), dependency.getArtifactId());
        assertEquals(dependenciesAfterPluginChange.get(0).getGroupId(), dependency.getGroupId());
        assertEquals(dependenciesAfterPluginChange.get(0).getVersion(), dependency.getVersion());
    }

    @Test
    public void removePluginDependenciesTest() throws IOException, XmlPullParserException {
        PomChangePlugin uut = new PomChangePlugin("org.maven.plugins", "maven-antrun-plugin").relative("pom.xml").removeDependencies();

        assertFalse(getPluginBeforeChange(uut).getDependencies().isEmpty());
        assertTrue(uut.isRemovePluginDependencies());
        executeAndAssertSuccess(uut);
        assertTrue(getPluginAfterChange(uut).getDependencies().isEmpty());
    }

    private Plugin getPluginBeforeChange(PomChangePlugin pomChangePlugin) throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        Plugin pluginBeforeChange = pomChangePlugin.getPlugin(pomModelBeforeChange);
        assertNotNull(pluginBeforeChange);
        return pluginBeforeChange;
    }

    private Plugin getPluginAfterChange(PomChangePlugin pomChangePlugin) throws IOException, XmlPullParserException {
        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        Plugin pluginAfterChange = pomChangePlugin.getPlugin(pomModelAfterChange);
        assertNotNull(pluginAfterChange);
        return pluginAfterChange;
    }

    private void executeAndAssertSuccess(AbstractPomOperation<?> pomOperation) {
        TOExecutionResult executionResult = pomOperation.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
    }


    private class PluginExecutionBuilder {
        private PluginExecution pluginExecution;

        PluginExecutionBuilder() {
            this.pluginExecution = new PluginExecution();
        }

        public PluginExecutionBuilder setId(String id){
            this.pluginExecution.setId(id);
            return this;
        }

        public PluginExecutionBuilder setPhase(String phase){
            this.pluginExecution.setPhase(phase);
            return this;
        }

        public PluginExecutionBuilder addGoal(String goal){
            this.pluginExecution.addGoal(goal);
            return this;
        }

        public PluginExecution build(){
            return this.pluginExecution;
        }
    }

}
