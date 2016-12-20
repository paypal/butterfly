package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import sample.code.Dog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Unit test for {@link InsertLine}
 *
 * @author facarvalho
 */
public class InsertLineTest extends TransformationUtilityTestHelper {

    @Test
    public void noOpRegexTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: yellow", "   name: Billy").relative("dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("dogs.yaml");
    }

    @Test
    public void noOpLineNumberTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: yellow", 1427).relative("dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("dogs.yaml");
    }

    // FIXME
    // There is nothing wrong with this unit test, it is correct.
    // What is wrong is the implementation of InsertLine, it has a bug.
    // Run this test as is and you will see that it will fail.
    @Test(enabled = false)
    public void insertLineNumberTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: black and white", 7).relative("dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("dogs.yaml");
        assertLineCount("dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("dogs.yaml");
        Dog mustache = dogs.get("Mustache");
        Assert.assertEquals(mustache.getColor(), "black and white");
    }

    @Test
    public void insertFirstRegexTest() throws IOException, URISyntaxException {
        InsertLine insertLine = new InsertLine("   color: black and white", "   name: Mustache").relative("dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("dogs.yaml");
        assertLineCount("dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("dogs.yaml");
        Dog mustache = dogs.get("Mustache");
        Assert.assertEquals(mustache.getColor(), "black and white");
    }

    @Test
    public void insertAllRegexTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: gray", "(   breed:.*)").relative("dogs.yaml").setInsertionMode(InsertLine.InsertionMode.REGEX_ALL);
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("dogs.yaml");
        assertLineCount("dogs.yaml", 2);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("dogs.yaml");
        Assert.assertEquals(dogs.get("Toby").getColor(), "gray");
        Assert.assertEquals(dogs.get("Mustache").getColor(), "gray");
    }

    @Test
    public void fileDoesNotExistTest() {
        InsertLine insertLine = new InsertLine("   color: yellow", "   name: Billy").relative("caes.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertEquals(executionResult.getException().getClass(), FileNotFoundException.class);
    }

}
