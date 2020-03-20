package com.paypal.butterfly.utilities.operations.pom;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

/**
 * Unit test class for {@link PomCopyManagedDependencies}
 *
 * @author facarvalho
 */
public class PomCopyManagedDependenciesTest extends TransformationUtilityTestHelper {

    @Test
    public void copyTest() throws IOException, XmlPullParserException {
        PomCopyManagedDependencies pomCopyManagedDependencies = new PomCopyManagedDependencies().relative("pom.xml")
                .setToRelative("src/main/resources/pom_less_dependencies.xml").filter("junit:junit");

        TOExecutionResult toExecutionResult = pomCopyManagedDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertNotChangedFile("pom.xml");
        assertChangedFile("src/main/resources/pom_less_dependencies.xml");
        Model transformedPomModel = getTransformedPomModel("src/main/resources/pom_less_dependencies.xml");

        assertEquals(transformedPomModel.getDependencyManagement().getDependencies().size(), 2);

        Set<String> expectedDependencies = new TreeSet<>();
        expectedDependencies.add("org.testng:testng");
        expectedDependencies.add("commons-io:commons-io");

        Set<String> actualDependencies = new TreeSet<>();
        transformedPomModel.getDependencyManagement().getDependencies().forEach(d -> actualDependencies.add(s(d)));

        assertEquals(actualDependencies, expectedDependencies);
    }

    @Test
    public void invalidDependencyStringTest() {
        try {
            new PomCopyManagedDependencies().filter("org.apache.commons.commons-lang3");
            fail("TransformationDefinitionException has not been thrown");
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "Maven dependency String representation 'org.apache.commons.commons-lang3' does not conform with format groupId:artifactId");
        }
        try {
            new PomCopyManagedDependencies().replace("org.apache.commons.commons-lang3", "org.lightcouch:lightcouch:1.0.0");
            fail("TransformationDefinitionException has not been thrown");
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "Maven dependency String representation 'org.apache.commons.commons-lang3' does not conform with format groupId:artifactId");
        }
    }

    @Test
    public void noOpTest1() throws IOException {
        PomCopyManagedDependencies pomCopyManagedDependencies = new PomCopyManagedDependencies().relative("pom.xml").setToRelative("src/main/resources/pom_less_dependencies.xml")
                .filter("junit:junit")
                .filter("commons-io:commons-io")
                .filter("org.apache.commons:commons-lang3");

        TOExecutionResult toExecutionResult = pomCopyManagedDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("pom.xml");
        assertNotChangedFile("src/main/resources/pom_less_dependencies.xml");
    }

    @Test
    public void noOpTest2() throws IOException {
        PomCopyManagedDependencies pomCopyManagedDependencies = new PomCopyManagedDependencies().relative("pom.xml").setToRelative("src/main/resources/no_parent_pom.xml");
        TOExecutionResult toExecutionResult = pomCopyManagedDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("pom.xml");
        assertNotChangedFile("src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void copyFilterFromAttributeTest() throws IOException, XmlPullParserException {
        Mockito.when(transformationContext.get("ATT")).thenReturn("junit:junit");
        Mockito.when(transformationContext.get("ATTnull")).thenReturn(null);
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.contains("ATTnull")).thenReturn(true);

        PomCopyManagedDependencies pomCopyManagedDependencies = new PomCopyManagedDependencies().relative("pom.xml")
                .setToRelative("src/main/resources/pom_less_dependencies.xml")
                .filterAttribute("ATT")
                .filterAttribute("ATTnull")
                .filterAttribute("tobeignored")
                .replace("commons-io:commons-io","org.lightcouch:lightcouch");

        TOExecutionResult toExecutionResult = pomCopyManagedDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertNotChangedFile("pom.xml");
        assertChangedFile("src/main/resources/pom_less_dependencies.xml");
        Model transformedPomModel = getTransformedPomModel("src/main/resources/pom_less_dependencies.xml");

        assertEquals(transformedPomModel.getDependencyManagement().getDependencies().size(), 2);

        Set<String> expectedDependencies = new TreeSet<>();
        expectedDependencies.add("org.testng:testng");
        expectedDependencies.add("org.lightcouch:lightcouch");

        Set<String> actualDependencies = new TreeSet<>();
        transformedPomModel.getDependencyManagement().getDependencies().forEach(d -> actualDependencies.add(s(d)));

        assertEquals(actualDependencies, expectedDependencies);
    }

    /*
     * Returns a String representation of this Maven dependency made of
     * group id and artifact id separated by colon
     */
    private String s(Dependency dependency) {
        return dependency.getGroupId() + ":" + dependency.getArtifactId();
    }

}
