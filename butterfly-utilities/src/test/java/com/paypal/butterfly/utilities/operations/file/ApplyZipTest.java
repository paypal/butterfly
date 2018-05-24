package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link ApplyZip}
 *
 * @author facarvalho
 */
public class ApplyZipTest extends TransformationUtilityTestHelper {

    @Test
    public void test() throws URISyntaxException, IOException {
        File testZipFile = new File(transformedAppFolder, "/src/main/resources/zipFile");
        assertFalse(testZipFile.exists());

        URL zipFileUrl = this.getClass().getResource("/zipFile.zip");
        ApplyZip applyZip = new ApplyZip().setZipFileUrl(zipFileUrl).relative("/src/main/resources");
        TOExecutionResult executionResult = applyZip.execution(transformedAppFolder, transformationContext);

        assertEquals(applyZip.getZipFileUrl(), zipFileUrl);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(applyZip.getDescription(), "Download, decompress and place contents of zip file " + zipFileUrl.getFile() + " at /src/main/resources");
        assertEquals(executionResult.getDetails(), "Zip file '" + zipFileUrl.getFile() + "' has been downloaded and decompressed into /src/main/resources");
        assertTrue(testZipFile.exists());

        File oneLineFile = new File(this.getClass().getResource("/oneLineFile.txt").toURI());
        File oneLineFile1 = new File(testZipFile, "oneLineFile1.txt");
        File oneLineFile2 = new File(testZipFile, "oneLineFile2.txt");

        assertTrue(oneLineFile1.exists());
        assertTrue(oneLineFile2.exists());
        assertTrue(FileUtils.contentEquals(oneLineFile1, oneLineFile));
        assertTrue(FileUtils.contentEquals(oneLineFile2, oneLineFile));
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Zip file URL cannot be null")
    public void nonExistentFileTest1() {
        URL nullFileUrl = this.getClass().getResource("/nonExistentFile.zip");
        new ApplyZip(nullFileUrl).relative("");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Malformed zip file URL")
    public void nonExistentFileTest2() {
        new ApplyZip("nonExistentFile.zip").relative("");
    }

    /*
     * Test trying to unzip a file that is not a zip file
     */
    @Test
    public void invalidFileTest() throws URISyntaxException {
        URL zipFileUrl = this.getClass().getResource("/not-a-real-zip-file.zip");

        ApplyZip applyZip = new ApplyZip(zipFileUrl).relative("/src/main/resources");
        TOExecutionResult executionResult = applyZip.execution(transformedAppFolder, transformationContext);

        assertEquals(applyZip.getZipFileUrl(), zipFileUrl);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "File could not be unzipped");
        assertEquals(executionResult.getException().getCause().getMessage(), "Probably not a zip file or a corrupted zip file");
        assertEquals(applyZip.getDescription(), "Download, decompress and place contents of zip file " + zipFileUrl.getFile() + " at /src/main/resources");
        assertNull(executionResult.getDetails());
    }

}
