package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Unit test class for {@link PomRemoveDependencyExclusion}
 *
 * @author asingh21
 */
public class PomRemoveDependencyExclusionTest extends TransformationUtilityTestHelper {

    private Exclusion testExclusion;

    @BeforeClass
    public void beforeClassHelper() {
        testExclusion = new Exclusion();
        testExclusion.setGroupId("com.google.guava");
        testExclusion.setArtifactId("guava");
    }

    @Test(dataProvider = "dependencyExclusionsToTest")
    public void happyPathTest(String dependencyGroupId,
                              String dependencyArtifactId,
                              String relativePomPath,
                              String expectedDescription,
                              boolean isManagedDependency,
                              int numExpectedExclusionsBeforeChange,
                              int numExpectedExclusionsAfterChange,
                              TOExecutionResult.Type type) throws IOException, XmlPullParserException {
        final PomRemoveDependencyExclusion test = new PomRemoveDependencyExclusion(
                dependencyGroupId,
                dependencyArtifactId,
                testExclusion.getGroupId(),
                testExclusion.getArtifactId())
                .relative(relativePomPath);

        expectedDescription = String.format(expectedDescription, testExclusion.getGroupId(),
                testExclusion.getArtifactId(), dependencyGroupId, dependencyArtifactId, relativePomPath);
        happyPathTest(test, expectedDescription, testExclusion, relativePomPath,
                isManagedDependency, numExpectedExclusionsBeforeChange, numExpectedExclusionsAfterChange,
                type);
    }

    @Test
    public void failNotPresentTest() throws IOException {
        PomRemoveDependencyExclusion pomReplaceDependency = new PomRemoveDependencyExclusion("foo", "bar", "newfoo", "newbar").relative("pom.xml").failIfNotPresent();
        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Exclusion newfoo:newbar has not been removed as dependency foo:bar in POM file /pom.xml because dependency is not present");
        assertNull(executionResult.getException().getCause());

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void warningNotPresentTest() {
        PomRemoveDependencyExclusion pomReplaceDependency = new PomRemoveDependencyExclusion("foo", "bar", "newfoo", "newbar").relative("pom.xml").warnIfNotPresent();
        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getWarnings().get(0).getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getWarnings().get(0).getMessage(), "Exclusion newfoo:newbar has not been removed as dependency foo:bar in POM file /pom.xml because dependency is not present");
    }

    @Test
    public void noOpNotPresentTest() throws IOException {
        PomRemoveDependencyExclusion pomReplaceDependency = new PomRemoveDependencyExclusion("foo", "bar", "newfoo", "newbar").relative("pom.xml").noOpIfNotPresent();
        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);

        assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        assertNull(executionResult.getException());
        assertEquals(executionResult.getDetails(), "Exclusion newfoo:newbar has not been removed as dependency foo:bar in POM file /pom.xml because dependency is not present");

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void fileDoesNotExistTest() {
        PomRemoveDependencyExclusion pomReplaceDependency = new PomRemoveDependencyExclusion("foo", "bar", "newfoo", "newbar")
                .relative("non_existent_file.xml")
                .warnIfNotPresent();

        TOExecutionResult executionResult = pomReplaceDependency.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "POM file could not be modified");
        assertEquals(executionResult.getException().getCause().getClass(), FileNotFoundException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "non_existent_file.xml").getAbsolutePath() + " (No such file or directory)");
    }

    private void happyPathTest(PomRemoveDependencyExclusion pomRemoveDependencyExclusion,
                               String expectedDescription,
                               Exclusion expectedExclusion,
                               String relativeFilePath,
                               boolean isManagedDependency,
                               int numExpectedExclusionsBeforeChange,
                               int numExpectedExclusionsAfterChange,
                               TOExecutionResult.Type type) throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel(relativeFilePath);

        assertEquals(pomRemoveDependencyExclusion.getDescription(), expectedDescription);
        assertEquals(pomRemoveDependencyExclusion.getExclusion().getGroupId(), expectedExclusion.getGroupId());
        assertEquals(pomRemoveDependencyExclusion.getExclusion().getArtifactId(), expectedExclusion.getArtifactId());
        final Dependency dependencyBeforeChange;
        if(isManagedDependency) {
            dependencyBeforeChange = pomRemoveDependencyExclusion.getManagedDependency(pomModelBeforeChange);
        } else {
            dependencyBeforeChange = pomRemoveDependencyExclusion.getDependency(pomModelBeforeChange);
        }
        assertNotNull(dependencyBeforeChange);
        assertEquals(dependencyBeforeChange.getExclusions().stream()
                .map(exclusion -> exclusion.getGroupId().equals(expectedExclusion.getGroupId())
                        && exclusion.getArtifactId().equals(expectedExclusion.getArtifactId()))
                .count(), numExpectedExclusionsBeforeChange);

        TOExecutionResult executionResult = pomRemoveDependencyExclusion.execution(transformedAppFolder, transformationContext);
        Model pomModelAfterChange = getTransformedPomModel(relativeFilePath);

        assertEquals(executionResult.getType(), type);
        final Dependency dependencyAfterChange;
        if(isManagedDependency) {
            dependencyAfterChange = pomRemoveDependencyExclusion.getManagedDependency(pomModelAfterChange);
        } else {
            dependencyAfterChange = pomRemoveDependencyExclusion.getDependency(pomModelAfterChange);
        }
        assertNotNull(dependencyAfterChange);
        assertEquals(dependencyAfterChange.getExclusions().stream()
                .map(exclusion -> exclusion.getGroupId().equals(expectedExclusion.getGroupId())
                        && exclusion.getArtifactId().equals(expectedExclusion.getArtifactId()))
                .count(), numExpectedExclusionsAfterChange);

    }

    @DataProvider
    public Object[][] dependencyExclusionsToTest(){
        return new Object[][] {
                { "org.apache.commons", "commons-lang3", "pomWithExclusion.xml", "Remove the exclusion %s:%s if present in dependency %s:%s in POM file %s", false, 1, 0, TOExecutionResult.Type.SUCCESS},
                { "junit", "junit", "pomWithExclusion.xml", "Remove the exclusion %s:%s if present in dependency %s:%s in POM file %s", true, 1, 0, TOExecutionResult.Type.SUCCESS },
                { "xmlunit", "xmlunit", "pomWithExclusion.xml", "Remove the exclusion %s:%s if present in dependency %s:%s in POM file %s", false, 0, 0, TOExecutionResult.Type.ERROR }
        };
    }

}
