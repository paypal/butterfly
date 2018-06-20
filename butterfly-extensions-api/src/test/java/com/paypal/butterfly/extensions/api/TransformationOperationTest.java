package com.paypal.butterfly.extensions.api;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Unit tests for {@link TransformationOperation}
 *
 * @author facarvalho
 */
public class TransformationOperationTest extends TestHelper {

    @Test
    public void performTest() throws IOException {
        TransformationOperation transformationOperation = getNewTestTransformationOperation().relative("pom.xml");

        assertFalse(transformationOperation.hasBeenPerformed());
        PerformResult performResult = transformationOperation.perform(transformedAppFolder, transformationContext);
        assertTrue(transformationOperation.hasBeenPerformed());

        assertNotNull(performResult);
        assertEquals(performResult.getType(), PerformResult.Type.EXECUTION_RESULT);
        assertEquals(performResult.getExecutionResult().getType(), TOExecutionResult.Type.NO_OP);

        TOExecutionResult executionResult = (TOExecutionResult) performResult.getExecutionResult();
        assertFalse(executionResult.isExceptionType());
        assertEquals(executionResult.getDetails(), "nothing to be changed");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = "Transformation operations must always save results")
    public void saveResultTest() {
        TransformationOperation transformationOperation = getNewTestTransformationOperation();
        assertTrue(transformationOperation.isSaveResult());
        transformationOperation.setSaveResult(false);
    }

    @Test
    public void readFileTest() throws IOException {
        TransformationOperation transformationOperation = getNewTestTransformationOperation().relative("pom.xml");
        File readFile = transformationOperation.getOrCreateReadFile(transformedAppFolder, transformationContext);
        FileUtils.contentEquals(new File(transformedAppFolder, "pom.xml"), readFile);
    }

    @Test
    public void nonexistentReadFileTest() {
        try {
            TransformationOperation transformationOperation = getNewTestTransformationOperation().relative("foo");
            transformationOperation.getOrCreateReadFile(transformedAppFolder, transformationContext);
            fail("transformationOperation.getOrCreateReadFile was supposed to have thrown an exception");
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Specified file does not exist: " + new File(transformedAppFolder, "foo").getAbsolutePath());
        }
    }

    @Test
    public void dirReadFileTest() {
        try {
            TransformationOperation transformationOperation = getNewTestTransformationOperation().relative("");
            transformationOperation.getOrCreateReadFile(transformedAppFolder, transformationContext);
            fail("transformationOperation.getOrCreateReadFile was supposed to have thrown an exception");
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Specified file is a directory: " + transformedAppFolder.getAbsolutePath());
        }
    }

}
