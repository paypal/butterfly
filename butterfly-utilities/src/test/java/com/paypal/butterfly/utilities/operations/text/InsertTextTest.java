package com.paypal.butterfly.utilities.operations.text;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import sample.code.Dog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Unit test for {@link InsertText}
 *
 * @author facarvalho
 */
public class InsertTextTest extends TransformationUtilityTestHelper {

    private URL billyURL = getClass().getResource("/billy.yaml");

    @Test
    public void noOpRegexTest() throws IOException {
        InsertText insertText = new InsertText(billyURL).relative("src/main/resources/dogs.yaml").setRegex("   breed: vira-lata").setInsertionMode(InsertText.InsertionMode.REGEX_FIRST);
        Assert.assertEquals(insertText.getDescription(), "Insert text from " + billyURL.getFile() + " to src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("src/main/resources/dogs.yaml");
    }

    @Test
    public void noOpLineNumberTest() throws IOException {
        InsertText insertText = new InsertText().setTextFileUrl(billyURL).relative("src/main/resources/dogs.yaml").setLineNumber(123).setInsertionMode(InsertText.InsertionMode.LINE_NUMBER);
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);

        assertNotChangedFile("src/main/resources/dogs.yaml");
    }

    @Test
    public void insertLineNumberTest() throws IOException {
        InsertText insertText = new InsertText(billyURL, 1).relative("src/main/resources/dogs.yaml").setInsertionMode(InsertText.InsertionMode.LINE_NUMBER);
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("src/main/resources/dogs.yaml");
        assertLineCount("src/main/resources/dogs.yaml", 3);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("src/main/resources/dogs.yaml");

        Assert.assertEquals(dogs.size(), 3);

        Dog dog = dogs.get("Billy");
        Assert.assertEquals(dog.getName(), "Billy");
        Assert.assertEquals(dog.getBreed(), "lab");
    }

    @Test
    public void insertFirstRegexTest() throws IOException {
        InsertText insertText = new InsertText(billyURL, "   breed: poodle").relative("src/main/resources/dogs.yaml").setInsertionMode(InsertText.InsertionMode.REGEX_FIRST);
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("src/main/resources/dogs.yaml");
        assertLineCount("src/main/resources/dogs.yaml", 3);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("src/main/resources/dogs.yaml");

        Assert.assertEquals(dogs.size(), 3);

        Dog dog = dogs.get("Billy");
        Assert.assertEquals(dog.getName(), "Billy");
        Assert.assertEquals(dog.getBreed(), "lab");
    }

    @Test
    public void insertAllRegexTest() throws IOException {
        URL changeDogs = getClass().getResource("/changeDogs.yaml");

        InsertText insertText = new InsertText(changeDogs).relative("src/main/resources/dogs.yaml").setRegex("   breed: .*").setInsertionMode(InsertText.InsertionMode.REGEX_ALL);
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("src/main/resources/dogs.yaml");
        assertLineCount("src/main/resources/dogs.yaml", 4);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("src/main/resources/dogs.yaml");

        Assert.assertEquals(dogs.size(), 2);

        Dog dog;

        dog = dogs.get("Toby");
        Assert.assertEquals(dog.getName(), "Toby");
        Assert.assertEquals(dog.getBreed(), "poodle");
        Assert.assertEquals(dog.getColor(), "white");
        Assert.assertEquals(dog.isFixed(), true);

        dog = dogs.get("Mustache");
        Assert.assertEquals(dog.getName(), "Mustache");
        Assert.assertEquals(dog.getBreed(), "pit bull");
        Assert.assertEquals(dog.getColor(), "white");
        Assert.assertEquals(dog.isFixed(), true);
    }

    @Test
    public void insertConcatTest() throws IOException {
        InsertText insertText = new InsertText(billyURL).relative("src/main/resources/dogs.yaml");
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);

        assertChangedFile("src/main/resources/dogs.yaml");
        assertLineCount("src/main/resources/dogs.yaml", 3);

        Map<String, Dog> dogs = (Map) getObjectFromYaml("src/main/resources/dogs.yaml");

        Assert.assertEquals(dogs.size(), 3);

        Dog dog = dogs.get("Billy");
        Assert.assertEquals(dog.getName(), "Billy");
        Assert.assertEquals(dog.getBreed(), "lab");
    }

    @Test
    public void fileDoesNotExistTest() {
        InsertText insertText = new InsertText(billyURL).relative("caes.yaml");
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertEquals(executionResult.getException().getClass(), FileNotFoundException.class);
    }

    @Test
    public void cloneTest() throws IOException {
        InsertText insertText = new InsertText(billyURL, "   breed: poodle").relative("src/main/resources/dogs.yaml").setInsertionMode(InsertText.InsertionMode.REGEX_FIRST);
        TOExecutionResult executionResult = insertText.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertChangedFile("src/main/resources/dogs.yaml");
        assertLineCount("src/main/resources/dogs.yaml", 3);
        Map<String, Dog> dogs = (Map) getObjectFromYaml("src/main/resources/dogs.yaml");
        Assert.assertEquals(dogs.size(), 3);
        Dog dog = dogs.get("Billy");
        Assert.assertEquals(dog.getName(), "Billy");
        Assert.assertEquals(dog.getBreed(), "lab");

        executionResult = insertText.clone().execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        assertChangedFile("src/main/resources/dogs.yaml");
        assertLineCount("src/main/resources/dogs.yaml", 3);
        dogs = (Map) getObjectFromYaml("src/main/resources/dogs.yaml");
        Assert.assertEquals(dogs.size(), 3);
        dog = dogs.get("Billy");
        Assert.assertEquals(dog.getName(), "Billy");
        Assert.assertEquals(dog.getBreed(), "lab");
    }

}
