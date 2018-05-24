package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link CopyFile}
 *
 * @author facarvalho
 */
public class CopyFileTest extends TransformationUtilityTestHelper {

    @Test
    public void test() throws IOException {
        File fromFile = new File(transformedAppFolder, "foo.xml");
        File toFile = new File(transformedAppFolder, "/src/main/resources/foo.xml");

        assertTrue(fromFile.exists());
        assertFalse(fromFile.isDirectory());
        assertFalse(toFile.exists());

        CopyFile copyFile = new CopyFile().relative("foo.xml").setToRelative("src/main/resources");
        TOExecutionResult executionResult = copyFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(copyFile.getDescription(), "Copy file foo.xml to src/main/resources");
        assertEquals(executionResult.getDetails(), "File 'foo.xml' has been copied to '/src/main/resources'");

        assertTrue(fromFile.exists());
        assertTrue(toFile.exists());
        assertTrue(FileUtils.contentEquals(fromFile, toFile));
    }

    @Test
    public void nonExistentFromFileTest() {
        CopyFile copyFile = new CopyFile().relative("nonExistentFile").setToRelative("src/main/resources");
        TOExecutionResult executionResult = copyFile.execution(transformedAppFolder, transformationContext);

        assertEquals(copyFile.getToRelative(), "src/main/resources");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "File could not be copied");
        assertEquals(executionResult.getException().getCause().getMessage(), "File " + new File(transformedAppFolder, "nonExistentFile").getAbsolutePath() + " does not exist");
        assertEquals(copyFile.getDescription(), "Copy file nonExistentFile to src/main/resources");
        assertNull(executionResult.getDetails());
    }

    @Test
    public void nonExistentToFolderTest() throws IOException {
        File fromFile = new File(transformedAppFolder, "foo.xml");
        File toFile = new File(transformedAppFolder, "fooFolder/foo.xml");

        assertTrue(fromFile.exists());
        assertFalse(fromFile.isDirectory());
        assertFalse(toFile.exists());
        assertFalse(toFile.getParentFile().exists());

        CopyFile copyFile = new CopyFile().relative("foo.xml").setToRelative("fooFolder");
        TOExecutionResult executionResult = copyFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(copyFile.getDescription(), "Copy file foo.xml to fooFolder");
        assertEquals(executionResult.getDetails(), "File 'foo.xml' has been copied to '/fooFolder'");

        assertTrue(fromFile.exists());
        assertTrue(toFile.exists());
        assertTrue(FileUtils.contentEquals(fromFile, toFile));
    }

    @Test
    public void nonFileTest() {
        CopyFile copyFile = new CopyFile().relative("blah").setToRelative("src/main/resources");
        TOExecutionResult executionResult = copyFile.execution(transformedAppFolder, transformationContext);

        assertEquals(copyFile.getToRelative(), "src/main/resources");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "File could not be copied");
        assertEquals(executionResult.getException().getCause().getMessage(), new File(transformedAppFolder, "blah").getAbsolutePath() + " (Is a directory)");
        assertEquals(copyFile.getDescription(), "Copy file blah to src/main/resources");
        assertNull(executionResult.getDetails());
    }

}
