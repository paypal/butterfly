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
 * Unit test class for {@link PomCopyDependencies}
 *
 * @author facarvalho
 */
public class PomCopyDependenciesTest extends TransformationUtilityTestHelper {

    @Test
    public void copyTest() throws IOException, XmlPullParserException {
        PomCopyDependencies pomCopyDependencies = new PomCopyDependencies().relative("pom.xml")
                .setToRelative("src/main/resources/pom_less_dependencies.xml").filter("xmlunit:xmlunit")
                .replace("org.apache.commons:commons-lang3","org.lightcouch:lightcouch");

        TOExecutionResult toExecutionResult = pomCopyDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertNotChangedFile("pom.xml");
        assertChangedFile("src/main/resources/pom_less_dependencies.xml");
        Model transformedPomModel = getTransformedPomModel("src/main/resources/pom_less_dependencies.xml");

        assertEquals(transformedPomModel.getDependencies().size(), 5);

        Set<String> expectedDependencies = new TreeSet<>();
        expectedDependencies.add("org.springframework.boot:spring-boot-dependencies");
        expectedDependencies.add("junit:junit");
        expectedDependencies.add("commons-io:commons-io");
        expectedDependencies.add("org.testng:testng");
        expectedDependencies.add("org.lightcouch:lightcouch");

        Set<String> actualDependencies = new TreeSet<>();
        transformedPomModel.getDependencies().forEach(d -> actualDependencies.add(s(d)));

        assertEquals(actualDependencies, expectedDependencies);
    }

    @Test
    public void invalidDependencyStringTest() {
        try {
            new PomCopyDependencies().filter("org.apache.commons.commons-lang3");
            fail("TransformationDefinitionException has not been thrown");
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "Maven dependency String representation 'org.apache.commons.commons-lang3' does not conform with format groupId:artifactId");
        }
        try {
            new PomCopyDependencies().replace("org.apache.commons.commons-lang3", "org.lightcouch:lightcouch:1.0.0");
            fail("TransformationDefinitionException has not been thrown");
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "Maven dependency String representation 'org.apache.commons.commons-lang3' does not conform with format groupId:artifactId");
        }
    }

    @Test
    public void noOpTest1() throws IOException {
        PomCopyDependencies pomCopyDependencies = new PomCopyDependencies().relative("pom.xml").setToRelative("src/main/resources/pom_less_dependencies.xml")
                .filter("xmlunit:xmlunit")
                .filter("org.springframework.boot:spring-boot-dependencies")
                .filter("junit:junit")
                .filter("commons-io:commons-io")
                .filter("org.testng:testng")
                .filter("org.apache.commons:commons-lang3");

        TOExecutionResult toExecutionResult = pomCopyDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("pom.xml");
        assertNotChangedFile("src/main/resources/pom_less_dependencies.xml");
    }

    @Test
    public void noOpTest2() throws IOException {
        PomCopyDependencies pomCopyDependencies = new PomCopyDependencies().relative("pom.xml").setToRelative("src/main/resources/no_parent_pom.xml");
        TOExecutionResult toExecutionResult = pomCopyDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("pom.xml");
        assertNotChangedFile("src/main/resources/no_parent_pom.xml");
    }

    @Test
    public void copyFilterFromAttributeTest() throws IOException, XmlPullParserException {
        Mockito.when(transformationContext.get("ATT")).thenReturn("xmlunit:xmlunit");
        Mockito.when(transformationContext.get("ATTnull")).thenReturn(null);
        Mockito.when(transformationContext.contains("ATT")).thenReturn(true);
        Mockito.when(transformationContext.contains("ATTnull")).thenReturn(true);

        PomCopyDependencies pomCopyDependencies = new PomCopyDependencies().relative("pom.xml")
                .setToRelative("src/main/resources/pom_less_dependencies.xml")
                .filterAttribute("ATT")
                .filterAttribute("ATTnull")
                .filterAttribute("tobeignored")
                .replace("org.apache.commons:commons-lang3","org.lightcouch:lightcouch");

        TOExecutionResult toExecutionResult = pomCopyDependencies.execution(transformedAppFolder, transformationContext);
        assertNotNull(toExecutionResult);
        assertEquals(toExecutionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertNotChangedFile("pom.xml");
        assertChangedFile("src/main/resources/pom_less_dependencies.xml");
        Model transformedPomModel = getTransformedPomModel("src/main/resources/pom_less_dependencies.xml");

        assertEquals(transformedPomModel.getDependencies().size(), 5);

        Set<String> expectedDependencies = new TreeSet<>();
        expectedDependencies.add("org.springframework.boot:spring-boot-dependencies");
        expectedDependencies.add("junit:junit");
        expectedDependencies.add("commons-io:commons-io");
        expectedDependencies.add("org.testng:testng");
        expectedDependencies.add("org.lightcouch:lightcouch");

        Set<String> actualDependencies = new TreeSet<>();
        transformedPomModel.getDependencies().forEach(d -> actualDependencies.add(s(d)));

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
