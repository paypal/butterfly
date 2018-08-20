package com.paypal.butterfly.utilities.operations.pom;

import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

/**
 * Unit test class for {@link PomAddManagedDependency}
 *
 * @author facarvalho
 */
public class PomAddManagedDependencyTest extends TransformationUtilityTestHelper {

    @Test
    public void addDependencyWithVersionTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("org.springframework.batch", "spring-batch-core", "3.0.7.RELEASE").relative("pom.xml");

        Assert.assertNull(getDependencyBeforeChange(uut));
        executeAndAssertSuccess(uut);
        Dependency dependencyAfterChange = getDependencyAfterChange(uut);
        Assert.assertNotNull(dependencyAfterChange);
        Assert.assertEquals(dependencyAfterChange.getGroupId(), "org.springframework.batch");
        Assert.assertEquals(dependencyAfterChange.getArtifactId(), "spring-batch-core");
        Assert.assertEquals(dependencyAfterChange.getVersion(), "3.0.7.RELEASE");
        Assert.assertEquals(dependencyAfterChange.getScope(), null);

    }

    @Test
    public void addDependencyWithScopeTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("org.springframework.batch", "spring-batch-core", "3.0.7.RELEASE").relative("pom.xml").setScope("provided");

        Assert.assertNull(getDependencyBeforeChange(uut));
        executeAndAssertSuccess(uut);
        Dependency dependencyAfterChange = getDependencyAfterChange(uut);
        Assert.assertNotNull(dependencyAfterChange);
        Assert.assertEquals(dependencyAfterChange.getGroupId(), "org.springframework.batch");
        Assert.assertEquals(dependencyAfterChange.getArtifactId(), "spring-batch-core");
        Assert.assertEquals(dependencyAfterChange.getVersion(), "3.0.7.RELEASE");
        Assert.assertEquals(dependencyAfterChange.getScope(), "provided");

    }

    @Test
    public void defaultIfPresentTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("junit", "junit", "4.12").relative("pom.xml");

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        Dependency dependencyAfterChange = getManagedDependencyInList(getTransformedPomModel("pom.xml"), "xmlunit", "xmlunit", "1.7");
        Assert.assertNull(dependencyAfterChange);

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void warnNotAddIfPresentTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("junit", "junit", "1.7").relative("pom.xml").warnNotAddIfPresent();

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        Assert.assertNull(executionResult.getException());
        Dependency dependencyAfterChange = getManagedDependencyInList(getTransformedPomModel("pom.xml"), "xmlunit", "xmlunit", "1.7");
        Assert.assertNull(dependencyAfterChange);

// FIXME
// Uncomment this when STAX based version of this TO is implemented
//        assertNotChangedFile("pom.xml");
    }

    @Test
    public void warnButAddIfPresentTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("junit", "junit", "4.11").relative("pom.xml").warnButAddIfPresent();

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.WARNING);
        Assert.assertNull(executionResult.getException());
        Dependency dependencyAfterChange = getManagedDependencyInList(getTransformedPomModel("pom.xml"), "junit", "junit", "4.11");
        Assert.assertNotNull(dependencyAfterChange);
    }

    @Test
    public void noOpIfPresentTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("junit", "junit", "4.11").relative("pom.xml").noOpIfPresent();

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertNull(executionResult.getException());
        Dependency dependencyAfterChange = getManagedDependencyInList(getTransformedPomModel("pom.xml"), "junit", "junit", "4.11");
        Assert.assertNull(dependencyAfterChange);

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void failIfPresentTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("junit", "junit", "4.11").relative("pom.xml").failIfPresent();

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        Dependency dependencyAfterChange = getManagedDependencyInList(getTransformedPomModel("pom.xml"), "junit", "junit", "4.11");
        Assert.assertNull(dependencyAfterChange);

        assertNotChangedFile("pom.xml");
    }

    @Test
    public void overwriteIfPresentTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("junit", "junit", "4.11").relative("pom.xml").overwriteIfPresent().setScope("test");

        TOExecutionResult executionResult = uut.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        Assert.assertNull(executionResult.getException());
        Dependency dependencyAfterChange = getManagedDependencyInList(getTransformedPomModel("pom.xml"), "junit", "junit", "4.11");
        Assert.assertNotNull(dependencyAfterChange);
        Assert.assertEquals(dependencyAfterChange.getScope(), "test");

    }

    @Test
    public void getDescriptionTest() throws IOException, XmlPullParserException {
        PomAddManagedDependency uut = new PomAddManagedDependency("org.testng", "testng", "1.1").relative("pom.xml");

        String description = uut.getDescription();
        Assert.assertEquals(description, "Add managed dependency org.testng:testng:1.1 to POM file pom.xml");
    }

    private Dependency getDependencyBeforeChange(PomAddManagedDependency pomAddManagedDependency) throws IOException, XmlPullParserException {
        Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
        Dependency dependencyBeforeChange = pomAddManagedDependency.getDependency(pomModelBeforeChange);
        return dependencyBeforeChange;
    }

    private Dependency getDependencyAfterChange(PomAddManagedDependency pomAddManagedDependency) throws IOException, XmlPullParserException {
        Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        Dependency dependencyAfterChange = pomAddManagedDependency.getManagedDependency(pomModelAfterChange);
        return dependencyAfterChange;
    }

    private void executeAndAssertSuccess(AbstractPomOperation<?> pomOperation) {
        TOExecutionResult executionResult = pomOperation.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
    }

    /**
     * Get dependency list from a maven model. Note: This is needed because
     * {@link AbstractArtifactPomOperation#getDependencyInList(List, String, String)}
     * does not accept a version as argument.
     */
    private Dependency getManagedDependencyInList(Model model, String groupId, String artifactId, String version) {
        List<Dependency> dependencyList = model.getDependencyManagement().getDependencies();
        if (dependencyList == null || dependencyList.size() == 0) {
            return null;
        }

        Dependency dependency = null;
        for (Dependency d : dependencyList) {
            if (d.getArtifactId().equals(artifactId) && d.getGroupId().equals(groupId)
                    && d.getVersion().equals(version)) {
                dependency = d;
                break;
            }
        }

        return dependency;
    }

}
