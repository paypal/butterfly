package com.paypal.butterfly.utilities.maven;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
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

        RelatedArtifacts relatedArtifacts = new RelatedArtifacts("com.test", "foo-parent", "1.0", "pomsList");
        TUExecutionResult executionResult = relatedArtifacts.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
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

}
