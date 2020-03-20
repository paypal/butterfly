package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link CopyDirectory}
 *
 * @author facarvalho
 */
public class CopyDirectoryTest extends TransformationUtilityTestHelper {

    @Test
    public void test() {
        File fromDir = new File(transformedAppFolder, "blah");
        File toDir = new File(transformedAppFolder, "/src/main/resources/blah");

        assertTrue(fromDir.exists());
        assertTrue(fromDir.isDirectory());
        assertFalse(toDir.exists());

        CopyDirectory copyDirectory = new CopyDirectory().relative("blah").setToRelative("src/main/resources/blah");
        TOExecutionResult executionResult = copyDirectory.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(copyDirectory.getDescription(), "Copy directory content from blah to src/main/resources/blah");
        assertEquals(executionResult.getDetails(), "Files from '/blah' have been copied to '/src/main/resources/blah'");

        // Since the actual copy operation is delegated to Apache Commons FileUtils.copyDirectory, the actual copy
        // operation won't be tested too extensively here

        assertTrue(fromDir.exists());
        assertTrue(fromDir.isDirectory());
        assertTrue(toDir.exists());
        assertTrue(toDir.isDirectory());
        assertEquals(fromDir.listFiles().length, toDir.listFiles().length);
    }

    @Test
    public void nonExistentFromFolderTest() {
        CopyDirectory copyDirectory = new CopyDirectory().relative("nonExistentFolder").setToRelative("src/main/resources");
        TOExecutionResult executionResult = copyDirectory.execution(transformedAppFolder, transformationContext);

        assertEquals(copyDirectory.getToRelative(), "src/main/resources");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Directory could not be copied");
        assertEquals(executionResult.getException().getCause().getMessage(), "Source '" + new File(transformedAppFolder, "nonExistentFolder").getAbsolutePath() + "' does not exist");
        assertEquals(copyDirectory.getDescription(), "Copy directory content from nonExistentFolder to src/main/resources");
        assertNull(executionResult.getDetails());
    }

    @Test
    public void nonDirectoryTest() {
        CopyDirectory copyDirectory = new CopyDirectory().relative("pom.xml").setToRelative("src/main/resources");
        TOExecutionResult executionResult = copyDirectory.execution(transformedAppFolder, transformationContext);

        assertEquals(copyDirectory.getToRelative(), "src/main/resources");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "Directory could not be copied");
        assertEquals(executionResult.getException().getCause().getMessage(), "Source '" + new File(transformedAppFolder, "pom.xml").getAbsolutePath() + "' exists but is not a directory");
        assertEquals(copyDirectory.getDescription(), "Copy directory content from pom.xml to src/main/resources");
        assertNull(executionResult.getDetails());
    }

}
