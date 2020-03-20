package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link PomReplaceDependency}
 *
 * @author facarvalho
 */
public class PomReplaceDependencyTest extends TransformationUtilityTestHelper {

    @Test
    public void happyPathTest1() throws IOException, XmlPullParserException {
        happyPathTest(new PomReplaceDependency(
                "org.apache.commons",
                "commons-lang3",
                "com.google.guava",
                "guava",
                "25.0-jre",
                "compile")
                .relative("pom.xml"));
    }

    @Test
    public void happyPathTest2() throws IOException, XmlPullParserException {
        happyPathTest(new PomReplaceDependency()
                .setGroupId("org.apache.commons")
                .setArtifactId("commons-lang3")
                .setNewGroupId("com.google.guava")
                .setNewArtifactId("guava")
                .setNewVersion("25.0-jre")
                .setNewScope("compile")
                .relative("pom.xml"));
    }

    @Test
    public void failNotPresentTest() throws IOException {
        PomReplaceDependency pomReplaceDependency = new PomReplaceDependency("foo", "bar", "newfoo", "newbar").relative("pom.xml").failIfNotPresent();
        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Dependency foo:bar has not been replaced by newfoo:newbar in POM file /pom.xml because it is not present");
        assertNull(executionResult.getException().getCause());

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void warningNotPresentTest() {
        PomReplaceDependency pomReplaceDependency = new PomReplaceDependency("foo", "bar", "newfoo", "newbar").relative("pom.xml").warnIfNotPresent();
        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Dependency foo:bar has not been replaced by newfoo:newbar in POM file /pom.xml because it is not present");

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void noOpNotPresentTest() throws IOException {
        PomReplaceDependency pomReplaceDependency = new PomReplaceDependency("foo", "bar", "newfoo", "newbar").relative("pom.xml").noOpIfNotPresent();
        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "Dependency foo:bar has not been replaced by newfoo:newbar in POM file /pom.xml because it is not present");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void fileDoesNotExistTest() {
        PomReplaceDependency pomReplaceDependency = new PomReplaceDependency("foo", "bar", "newfoo", "newbar")
                .relative("non_existent_file.xml")
                .warnIfNotPresent();

        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "POM file could not be modified");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "non_existent_file.xml").getAbsolutePath() + " (No such file or directory)");
    }

    private void happyPathTest(PomReplaceDependency pomReplaceDependency) throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");

        assertEquals(pomReplaceDependency.getDescription(), "Replace dependency org.apache.commons:commons-lang3 by com.google.guava:guava in POM file pom.xml");
        assertEquals(pomReplaceDependency.getGroupId(), "org.apache.commons");
        assertEquals(pomReplaceDependency.getArtifactId(), "commons-lang3");
        assertEquals(pomReplaceDependency.getNewGroupId(), "com.google.guava");
        assertEquals(pomReplaceDependency.getNewArtifactId(), "guava");
        assertEquals(pomReplaceDependency.getNewVersion(), "25.0-jre");
        assertEquals(pomReplaceDependency.getNewScope(), "compile");

        assertNotNull(pomReplaceDependency.getDependency(pomModelBeforeChange));

        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);
        Model pomModelAfterChange = getTransformedPomModel("pom.xml");

        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertNull(pomReplaceDependency.getDependency(pomModelAfterChange));
        assertNotNull(pomReplaceDependency.getDependency(pomModelAfterChange, "com.google.guava", "guava"));
        assertEquals(pomReplaceDependency.getDependency(pomModelAfterChange, "com.google.guava", "guava").getVersion(), "25.0-jre");
        assertEquals(pomReplaceDependency.getDependency(pomModelAfterChange, "com.google.guava", "guava").getScope(), "compile");
    }

}
