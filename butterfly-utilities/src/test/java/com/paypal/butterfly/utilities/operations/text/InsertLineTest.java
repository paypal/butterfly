package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import sample.code.Dog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

/**
 * Unit test for {@link InsertLine}
 *
 * @author facarvalho
 */
public class InsertLineTest extends TransformationUtilityTestHelper {

    @Test
    public void noOpRegexTest() throws IOException {
        InsertLine insertLine = new InsertLine().setNewLine("   color: yellow").setRegex("   name: Billy").relative("/src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(insertLine.getDescription(), "Insert new line(s) into /src/main/resources/dogs.yaml");
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("/src/main/resources/dogs.yaml");
    }

    @Test
    public void noOpLineNumberTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: yellow", 1427).relative("/src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("/src/main/resources/dogs.yaml");
    }

    @Test
    public void insertLineNumberTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: black and white", 6).relative("/src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");
        Dog mustache = dogs.get("Mustache");
        Assert.assertEquals(mustache.getColor(), "black and white");
    }

    @Test
    public void insertFirstRegexTest() throws IOException, URISyntaxException {
        InsertLine insertLine = new InsertLine("   color: black and white", "   name: Mustache").relative("/src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");
        Dog mustache = dogs.get("Mustache");
        Assert.assertEquals(mustache.getColor(), "black and white");
    }

    @Test
    public void insertAllRegexTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: gray", "(   breed:.*)").relative("/src/main/resources/dogs.yaml").setInsertionMode(InsertLine.InsertionMode.REGEX_ALL);
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 2);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");
        Assert.assertEquals(dogs.get("Toby").getColor(), "gray");
        Assert.assertEquals(dogs.get("Mustache").getColor(), "gray");
    }

    @Test
    public void insertRegexAtEndTest() throws IOException {
        InsertLine insertLine = new InsertLine("   fixed: false", "(.*breed: pit bull.*)").relative("/src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");

        Assert.assertEquals(dogs.size(), 2);

        Dog dog = dogs.get("Mustache");
        Assert.assertEquals(dog.getName(), "Mustache");
        Assert.assertEquals(dog.getBreed(), "pit bull");
        Assert.assertEquals(dog.isFixed(), false);
    }

    @Test
    public void insertFirstBeforeRegexTest() throws IOException, URISyntaxException {
        InsertLine insertLine = new InsertLine("   color: black and white", "Mustache: !sample\\.code\\.Dog").relative("/src/main/resources/dogs.yaml");
        insertLine.setInsertionMode(InsertLine.InsertionMode.REGEX_BEFORE_FIRST);
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");
        Dog mustache = dogs.get("Toby");
        Assert.assertEquals(mustache.getColor(), "black and white");
    }

    @Test
    public void insertAllBeforeRegexTest() throws IOException {
        InsertLine insertLine = new InsertLine("   color: gray", "(   breed:.*)").relative("/src/main/resources/dogs.yaml");
        insertLine.setInsertionMode(InsertLine.InsertionMode.REGEX_BEFORE_ALL);
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 2);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");
        Assert.assertEquals(dogs.get("Toby").getColor(), "gray");
        Assert.assertEquals(dogs.get("Mustache").getColor(), "gray");
    }

    @Test
    public void insertBeforeRegexAtEndTest() throws IOException {
        InsertLine insertLine = new InsertLine("   fixed: false", "(.*breed: pit bull.*)").relative("/src/main/resources/dogs.yaml");
        insertLine.setInsertionMode(InsertLine.InsertionMode.REGEX_BEFORE_FIRST);
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");

        Assert.assertEquals(dogs.size(), 2);

        Dog dog = dogs.get("Mustache");
        Assert.assertEquals(dog.getName(), "Mustache");
        Assert.assertEquals(dog.getBreed(), "pit bull");
        Assert.assertEquals(dog.isFixed(), false);
    }

    @Test
    public void insertConcatTest() throws IOException {
        InsertLine insertLine = new InsertLine("   fixed: false").relative("/src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/dogs.yaml");
        assertLineCount("/src/main/resources/dogs.yaml", 1);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("/src/main/resources/dogs.yaml");

        Assert.assertEquals(dogs.size(), 2);

        Dog dog = dogs.get("Mustache");
        Assert.assertEquals(dog.getName(), "Mustache");
        Assert.assertEquals(dog.getBreed(), "pit bull");
        Assert.assertEquals(dog.isFixed(), false);
    }

    @Test
    public void oneLineNoEOLTest() throws IOException {
        RemoveLine removeLine = new RemoveLine("(.*foo.*)").relative("/src/main/resources/application.properties").setFirstOnly(false);
        TOExecutionResult executionResult = removeLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", -2);

        Properties properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 1);
        Assert.assertEquals(properties.getProperty("bar"), "barv");

        InsertLine insertLine = new InsertLine("p1=p1v", "(.*barv.*)").relative("/src/main/resources/application.properties");
        executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("/src/main/resources/application.properties");
        assertLineCount("/src/main/resources/application.properties", -1);

        properties = getProperties("/src/main/resources/application.properties");

        Assert.assertEquals(properties.size(), 2);
        Assert.assertEquals(properties.getProperty("bar"), "barv");
        Assert.assertEquals(properties.getProperty("p1"), "p1v");
    }

    @Test
    public void fileDoesNotExistTest() {
        InsertLine insertLine = new InsertLine("   color: yellow", "   name: Billy").relative("/src/main/resources/caes.yaml");
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertEquals(executionResult.getException().getClass(), FileNotFoundException.class);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Line number cannot be negative or zero")
    public void invalidLineNumberTest1() {
        InsertLine insertLine = new InsertLine("   color: yellow").relative("/src/main/resources/dogs.yaml");
        insertLine.setLineNumber(-1);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class, expectedExceptionsMessageRegExp = "Line number cannot be negative or zero")
    public void invalidLineNumberTest2() {
        InsertLine insertLine = new InsertLine("   color: yellow").relative("/src/main/resources/dogs.yaml");
        insertLine.setLineNumber(0);
    }

    @Test
    public void invalidLineNumberTest3() {
        InsertLine insertLine = new InsertLine("   color: yellow").relative("/src/main/resources/dogs.yaml");
        insertLine.setLineNumber(39);
        TOExecutionResult executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertNull(executionResult.getException());

        insertLine.clone().setLineNumber(7);
        executionResult = insertLine.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertNull(executionResult.getException());
    }

}
