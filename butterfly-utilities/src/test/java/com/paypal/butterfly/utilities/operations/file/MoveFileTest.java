package com.paypal.butterfly.utilities.operations.file;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Unit test class for {@link MoveFile}
 *
 * @author facarvalho
 */
public class MoveFileTest extends TransformationUtilityTestHelper {

    @Test
    public void test() throws IOException {
        File originalFile = new File(transformedAppFolder, "foo.xml");
        File toDir = new File(transformedAppFolder, "/src/main/resources");

        assertTrue(originalFile.exists());
        assertTrue(originalFile.isFile());
        assertTrue(toDir.exists());

        // Saving original file as a temp file to have its content compared later
        File tempOriginalFile = File.createTempFile("butterfly-test-file", null);
        FileUtils.copyFile(originalFile, tempOriginalFile);

        Mockito.when(transformationContext.get("ATT")).thenReturn(new File(transformedAppFolder, "/src/main/resources"));
        MoveFile moveFile = new MoveFile().relative("foo.xml").setToAbsolute("ATT");
        TOExecutionResult executionResult = moveFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(moveFile.getToAbsoluteAttribute(), "ATT");
        assertEquals(moveFile.getDescription(), "Move file foo.xml to the location defined by transformation context attribute ATT");
        assertEquals(executionResult.getDetails(), "File 'foo.xml' has been moved to '/src/main/resources'");

        File copiedFile = new File(transformedAppFolder, "/src/main/resources/foo.xml");
        assertFalse(originalFile.exists());
        assertTrue(copiedFile.exists());
        assertTrue(FileUtils.contentEquals(tempOriginalFile, copiedFile));

        tempOriginalFile.delete();
    }

    @Test
    public void nonExistentFileTest() {
        MoveFile moveFile = new MoveFile().relative("nonExistentFile").setToRelative("src/main/resources");
        TOExecutionResult executionResult = moveFile.execution(transformedAppFolder, transformationContext);

        assertEquals(moveFile.getToRelative(), "src/main/resources");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "File could not be moved");
        assertEquals(executionResult.getException().getCause().getMessage(), "File " + new File(transformedAppFolder, "nonExistentFile").getAbsolutePath() + " does not exist");
        assertEquals(moveFile.getDescription(), "Move file nonExistentFile to src/main/resources");
        assertNull(executionResult.getDetails());
    }

    @Test
    public void nonExistentDirTest() throws IOException {
        File originalFile = new File(transformedAppFolder, "foo.xml");
        File toDir = new File(transformedAppFolder, "bar");

        assertTrue(originalFile.exists());
        assertTrue(originalFile.isFile());
        assertFalse(toDir.exists());

        // Saving original file as a temp file to have its content compared later
        File tempOriginalFile = File.createTempFile("butterfly-test-file", null);
        FileUtils.copyFile(originalFile, tempOriginalFile);

        MoveFile moveFile = new MoveFile().relative("foo.xml").setToRelative("bar");
        TOExecutionResult executionResult = moveFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(moveFile.getDescription(), "Move file foo.xml to bar");
        assertEquals(executionResult.getDetails(), "File 'foo.xml' has been moved to '/bar'");

        File movedFile = new File(transformedAppFolder, "bar/foo.xml");
        assertFalse(originalFile.exists());
        assertTrue(movedFile.exists());
        assertTrue(FileUtils.contentEquals(tempOriginalFile, movedFile));

        tempOriginalFile.delete();
    }

    @Test
    public void existentToFileTest() throws IOException {
        File originalFile = new File(transformedAppFolder, "src/main/resources/dogs.yaml");
        File toDir = new File(transformedAppFolder, "/src/main/resources/more_yaml/");

        assertTrue(originalFile.exists());
        assertTrue(originalFile.isFile());
        assertTrue(toDir.exists());
        assertTrue(new File(toDir, "dogs.yaml").exists());

        // Saving original file as a temp file to have its content compared later
        File tempOriginalFile = File.createTempFile("butterfly-test-file", null);
        FileUtils.copyFile(originalFile, tempOriginalFile);

        MoveFile moveFile = new MoveFile().relative("src/main/resources/dogs.yaml").setToRelative("src/main/resources/more_yaml");
        TOExecutionResult executionResult = moveFile.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertEquals(moveFile.getDescription(), "Move file src/main/resources/dogs.yaml to src/main/resources/more_yaml");
        assertEquals(executionResult.getDetails(), "File 'src/main/resources/dogs.yaml' has been moved to '/src/main/resources/more_yaml'");

        File movedFile = new File(transformedAppFolder, "src/main/resources/more_yaml/dogs.yaml");
        assertFalse(originalFile.exists());
        assertTrue(movedFile.exists());
        assertTrue(FileUtils.contentEquals(tempOriginalFile, movedFile));

        tempOriginalFile.delete();
    }

    @Test
    public void nonFileTest() {
        MoveFile moveFile = new MoveFile().relative("blah").setToRelative("src");
        TOExecutionResult executionResult = moveFile.execution(transformedAppFolder, transformationContext);

        assertEquals(moveFile.getToRelative(), "src");
        assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        assertEquals(executionResult.getException().getMessage(), "File could not be moved");
        assertEquals(executionResult.getException().getCause().getClass(), IOException.class);
        assertEquals(executionResult.getException().getCause().getMessage(), "/blah (Is a directory)");
        assertEquals(moveFile.getDescription(), "Move file blah to src");
        assertNull(executionResult.getDetails());
    }

}
