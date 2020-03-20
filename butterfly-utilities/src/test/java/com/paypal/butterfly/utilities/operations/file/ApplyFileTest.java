package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link ApplyFile}
 *
 * @author facarvalho
 */
public class ApplyFileTest extends TransformationUtilityTestHelper {

    @Test
    public void test() throws URISyntaxException, IOException {
        File testFile = new File(transformedAppFolder, "oneLineFile.txt");
        assertFalse(testFile.exists());

        URL fileUrl = this.getClass().getResource("/oneLineFile.txt");
        ApplyFile applyFile = new ApplyFile().setFileUrl(fileUrl).relative("");
        TOExecutionResult executionResult = applyFile.execution(transformedAppFolder, transformationContext);

        assertEquals(applyFile.getFileUrl(), fileUrl);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(applyFile.getDescription(), "Download and place file " + fileUrl.getFile() + " at the root folder");
        assertEquals(executionResult.getDetails(), "File '" + fileUrl.getFile() + "' has been downloaded at the root folder");
        assertTrue(testFile.exists());
        assertTrue(FileUtils.contentEquals(new File(fileUrl.toURI()), testFile));
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "File URL cannot be null")
    public void nonExistentFileTest1() {
        URL nullFileUrl = this.getClass().getResource("/nonExistentFile.txt");
        new ApplyFile(nullFileUrl).relative("");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Malformed file URL")
    public void nonExistentFileTest2() {
        new ApplyFile("nonExistentFile.txt").relative("");
    }

}
