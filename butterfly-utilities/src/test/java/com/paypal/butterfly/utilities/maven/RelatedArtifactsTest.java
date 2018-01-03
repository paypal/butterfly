package com.paypal.butterfly.utilities.maven;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link RelatedArtifacts}
 *
 * @author facarvalho
 */
public class RelatedArtifactsTest extends TransformationUtilityTestHelper {

    @Test
    public void test() throws URISyntaxException {
        File file = new File(getClass().getResource("/test-app/pom.xml").toURI());
        Mockito.when(transformationContext.get("pomsList")).thenReturn(Arrays.asList(file));

        RelatedArtifacts relatedArtifacts = new RelatedArtifacts();
        relatedArtifacts.setParentGroupId("com.test").setParentArtifactId("foo-parent").setParentVersion("1.0").setPomFilesAttribute("pomsList");
        TUExecutionResult executionResult = relatedArtifacts.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertEquals(relatedArtifacts.getParentGroupId(), "com.test");
        Assert.assertEquals(relatedArtifacts.getParentArtifactId(), "foo-parent");
        Assert.assertEquals(relatedArtifacts.getParentVersion(), "1.0");
        Assert.assertEquals(relatedArtifacts.getPomFilesAttribute(), "pomsList");
        Assert.assertNotNull(executionResult.getValue());
        List<File> pomFiles = (List<File>) executionResult.getValue();
        Assert.assertEquals(pomFiles.size(), 1);
        Assert.assertEquals(relatedArtifacts.getDescription(), "Identifies all pom files whose parent is com.test:foo-parent:1.0, directly or indirectly");
    }

    @Test
    public void noChildrenTest() throws URISyntaxException {
        File file = new File(getClass().getResource("/test-app/pom.xml").toURI());
        Mockito.when(transformationContext.get("pomsList")).thenReturn(Arrays.asList(file));

        RelatedArtifacts relatedArtifacts = new RelatedArtifacts("com.test", "bar-parent", "1.0", "pomsList");
        TUExecutionResult executionResult = relatedArtifacts.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        List<File> pomFiles = (List<File>) executionResult.getValue();
        Assert.assertEquals(pomFiles.size(), 0);
        Assert.assertEquals(relatedArtifacts.getDescription(), "Identifies all pom files whose parent is com.test:bar-parent:1.0, directly or indirectly");
    }

    @Test
    public void noPomFileTest() throws URISyntaxException {
        File file = new File(getClass().getResource("/test-app/src/main/resources/application.properties").toURI());
        Mockito.when(transformationContext.get("pomsList")).thenReturn(Arrays.asList(file));

        RelatedArtifacts relatedArtifacts = new RelatedArtifacts("com.test", "bar-parent", "1.0", "pomsList");
        TUExecutionResult executionResult = relatedArtifacts.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(relatedArtifacts.getDescription(), "Identifies all pom files whose parent is com.test:bar-parent:1.0, directly or indirectly");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "Error when trying to create Maven pom file model, double check if this file has a valid Maven structure: " + file.getAbsolutePath());
    }

}
