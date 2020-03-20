package com.paypal.butterfly.utilities.pom;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Unit test for {@link PomModel}
 *
 * @author vkuncham, radkrish
 */
public class PomModelTest extends TransformationUtilityTestHelper {

    @Test
    public void descriptionTest() {
        PomModel pomModel = new PomModel().setGroupId("junit").setArtifactId("junit").setVersion("4.12");
        assertEquals(pomModel.getGroupId(), "junit");
        assertEquals(pomModel.getArtifactId(), "junit");
        assertEquals(pomModel.getVersion(), "4.12");
        assertEquals(pomModel.getRepoURI(), "https://repo1.maven.org/maven2");
        assertEquals(pomModel.getDescription(), "Retrieve the parent pom and load it in to Model Object");
    }

    @Test
    public void improperArtifactInfoTest() {
        try {
            new PomModel().setArtifact("asdf");
            assertTrue(false);
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "Artifact info should be specified as [groupId]:[artifactId]:[version]");
        }
    }

    @Test
    public void nullArtifactInfoTest() {
        try {
            new PomModel().setArtifact(null);
            assertTrue(false);
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "artifactInfo cannot be blank");
        }
    }

    @Test
    public void nullGroupIdTest() {
        try {
            new PomModel(null, "junit", "4.12");
            assertTrue(false);
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "groupId cannot be blank");
        }

    }

    @Test
    public void nullArtifactIdTest() {
        try {
            new PomModel("junit", null, "4.12");
            assertTrue(false);
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "artifactId cannot be blank");
        }
    }

    @Test
    public void nullVersionTest() {
        try {
            new PomModel("junit", "junit", "");
            assertTrue(false);
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "version cannot be blank");
        }

    }

    @Test
    public void notValidURITest() {
        try {
            new PomModel("junit", "junit", "4.12", "/maven.org");
            assertTrue(false);
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "repoURI is not a valid URI");
        }
    }

    @Test
    public void fetchModelFromRemoteTestWithArtifactInfo() {
        String artifactInfo = "junit:junit:4.12";

        PomModel pomModel = new PomModel().setArtifact(artifactInfo);
        TUExecutionResult executionResult = pomModel.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Model model = (Model) executionResult.getValue();

        assertEquals("junit", model.getGroupId());
        assertEquals("junit", model.getArtifactId());
        assertEquals("4.12", model.getVersion());
    }

    @Test
    public void fetchModelFromRemoteTest() {
        String groupId = "junit";
        String artifactId = "junit";
        String version = "4.12";

        PomModel pomModel = new PomModel().setGroupId("junit").setArtifactId("junit").setVersion("4.12");
        TUExecutionResult executionResult = pomModel.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Model model = (Model) executionResult.getValue();

        assertEquals(groupId, model.getGroupId());
        assertEquals(artifactId, model.getArtifactId());
        assertEquals(version, model.getVersion());
    }

    @Test
    public void fetchModelFromRemoteExceptionTest() {
        String groupId = "junit";
        String artifactId = "junit";
        String version = "4.12.123123";

        PomModel pomModel = new PomModel(groupId, artifactId, version);
        TUExecutionResult executionResult = pomModel.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "The specified file could not be found or read and parsed as valid Maven pom file");
    }

    @Test
    public void fetchModelFromLocalTest() throws IOException, XmlPullParserException {
        Model originalPomModel = getOriginalPomModel("pom.xml");

        PomModel pomModel = new PomModel().relative("pom.xml");
        TUExecutionResult executionResult = pomModel.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Model model = (Model) executionResult.getValue();

        assertEquals(originalPomModel.getGroupId(), model.getGroupId());
        assertEquals(originalPomModel.getArtifactId(), model.getArtifactId());
        assertEquals(originalPomModel.getVersion(), model.getVersion());
        assertEquals(originalPomModel.getPackaging(), model.getPackaging());
        assertEquals(originalPomModel.getName(), model.getName());
        assertEquals(originalPomModel.getDescription(), model.getDescription());
    }

    @Test
    public void noCoordinatesNoFileExceptionTest() {
        PomModel pomModel = new PomModel().setGroupId("junit").setArtifactId("junit");
        TUExecutionResult executionResult = pomModel.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "Maven coordinates are missing and local file was not set");
    }

}
