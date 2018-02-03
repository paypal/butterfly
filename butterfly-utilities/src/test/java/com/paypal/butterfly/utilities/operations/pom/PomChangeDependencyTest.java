package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public class PomChangeDependencyTest extends TransformationUtilityTestHelper {

    @Test
    public void addVersionTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").setVersion("6.11");

        assertEquals(getDependencyBeforeChange(uut).getVersion(), null);
        executeAndAssertSuccess(uut);
        assertEquals(getDependencyAfterChange(uut).getVersion(), "6.11");
    }

    @Test
    public void changeVersionTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.apache.commons", "commons-lang3").relative("pom.xml").setVersion("3.5");

        assertEquals(getDependencyBeforeChange(uut).getVersion(), "3.4");
        executeAndAssertSuccess(uut);
        assertEquals(getDependencyAfterChange(uut).getVersion(), "3.5");
    }

    @Test
    public void removeVersionTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.apache.commons", "commons-lang3").relative("pom.xml").removeVersion();

        assertEquals(getDependencyBeforeChange(uut).getVersion(), "3.4");
        executeAndAssertSuccess(uut);
        assertNull(getDependencyAfterChange(uut).getVersion());
    }

    @Test
    public void addScopeTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("xmlunit", "xmlunit").relative("pom.xml").setScope("test");

        assertEquals(getDependencyBeforeChange(uut).getScope(), null);
        executeAndAssertSuccess(uut);
        assertEquals(getDependencyAfterChange(uut).getScope(), "test");
    }

    @Test
    public void changeScopeTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").setScope("compile");

        assertEquals(getDependencyBeforeChange(uut).getScope(), "test");
        executeAndAssertSuccess(uut);
        assertEquals(getDependencyAfterChange(uut).getScope(), "compile");
    }

    @Test
    public void removeScopeTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").removeScope();

        assertEquals(getDependencyBeforeChange(uut).getScope(), "test");
        executeAndAssertSuccess(uut);
        assertEquals(getDependencyAfterChange(uut).getScope(), null);
    }

    @Test
    public void addTypeTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").setType("pom");

        // Dependency with no type always returns "jar" as the type
        assertEquals(getDependencyBeforeChange(uut).getType(), "jar");
        executeAndAssertSuccess(uut);
        assertEquals(getDependencyAfterChange(uut).getType(), "pom");
    }

    @Test
    public void changeTypeTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.springframework.boot", "spring-boot-dependencies").relative("pom.xml").setType("jar");

        assertEquals(getDependencyBeforeChange(uut).getType(), "pom");
        executeAndAssertSuccess(uut);
        assertEquals(getDependencyAfterChange(uut).getType(), "jar");
    }

    @Test
    public void removeTypeTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.springframework.boot", "spring-boot-dependencies").relative("pom.xml").removeType();

        assertEquals(getDependencyBeforeChange(uut).getType(), "pom");
        executeAndAssertSuccess(uut);
        // Removing the type sets it to "jar"
        assertEquals(getDependencyAfterChange(uut).getType(), "jar");
    }

    @Test
    public void addOptionalTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml").setOptional();

        assertFalse(getDependencyBeforeChange(uut).isOptional());
        executeAndAssertSuccess(uut);
        assertTrue(getDependencyAfterChange(uut).isOptional());
    }

    @Test
    public void changeOptionalTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("junit", "junit").relative("pom.xml").setOptional();

        assertFalse(getDependencyBeforeChange(uut).isOptional());
        executeAndAssertSuccess(uut);
        assertTrue(getDependencyAfterChange(uut).isOptional());
    }

    @Test
    public void removeOptionalTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("commons-io", "commons-io").relative("pom.xml").removeOptional();

        assertTrue(getDependencyBeforeChange(uut).isOptional());
        executeAndAssertSuccess(uut);
        assertFalse(getDependencyAfterChange(uut).isOptional());
    }

    @Test
    public void defaultNotPresentTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present").relative("pom.xml");

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertNotNull(executionResult.getException());
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void warnIfNotPresentTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present").relative("pom.xml").warnIfNotPresent();

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void failIfNotPresentTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present").relative("pom.xml").failIfNotPresent();

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertNotNull(executionResult.getException());
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void noOpIfNotPresentTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("com.paypal", "not-present").relative("pom.xml").noOpIfNotPresent();

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void getDescriptionTest() throws IOException, XmlPullParserException {
        PomChangeDependency uut = new PomChangeDependency("org.testng", "testng").relative("pom.xml");

        String description = uut.getDescription();
        assertEquals(description, "Change dependency org.testng:testng in POM file pom.xml");
    }

    private Dependency getDependencyBeforeChange(PomChangeDependency pomChangeDependency) throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        Dependency dependencyBeforeChange = pomChangeDependency.getDependency(pomModelBeforeChange);
        assertNotNull(dependencyBeforeChange);
        return dependencyBeforeChange;
    }

    private Dependency getDependencyAfterChange(PomChangeDependency pomChangeDependency) throws IOException, XmlPullParserException {
        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        Dependency dependencyAfterChange = pomChangeDependency.getDependency(pomModelAfterChange);
        assertNotNull(dependencyAfterChange);
        return dependencyAfterChange;
    }

    private void executeAndAssertSuccess(AbstractPomOperation<?> pomOperation) {
        TOExecutionResult executionResult = pomOperation.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
    }

}
