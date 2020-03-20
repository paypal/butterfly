package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link PomChangePackaging}
 *
 * @author facarvalho
 */
public class PomChangePackagingTest extends TransformationUtilityTestHelper {

    @Test
    public void miscTest() {
        PomChangePackaging pomChangePackaging = new PomChangePackaging().setPackagingType("war").relative("pom.xml");

        assertEquals(pomChangePackaging.getPackagingType(), "war");
        assertEquals(pomChangePackaging.getDescription(), "Change packaging to war in POM file pom.xml");
        assertEquals(pomChangePackaging.clone(), pomChangePackaging);
    }

	@Test
	public void pomChangePackagingTest() throws IOException, XmlPullParserException {
		Model pomModelBeforeChange = getOriginalPomModel("pom.xml");
		assertEquals(pomModelBeforeChange.getPackaging(), "jar");

        PomChangePackaging pomChangePackaging = new PomChangePackaging("war").relative("pom.xml");
		TOExecutionResult executionResult = pomChangePackaging.execution(transformedAppFolder, transformationContext);
		assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

		Model pomModelAfterChange = getTransformedPomModel("pom.xml");
        assertEquals(pomModelAfterChange.getPackaging(), "war");
	}

}