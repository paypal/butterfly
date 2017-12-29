package com.paypal.butterfly.utilities.file;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Unit tests for {@link FindFiles}
 *
 * @author facarvalho
 */
public class FindFilesTest extends TransformationUtilityTestHelper {

    @Test
    public void recursiveMultipleFilesFoundTest() {
        FindFiles findFiles =  new FindFiles("(.*\\.xml)", true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 4);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/pom.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/webapp/WEB-INF/web.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/copy_of_web.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/no_parent_pom.xml")));

        Assert.assertEquals(findFiles.getNameRegex(), "(.*\\.xml)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void nonRecursiveSingleFileFoundTest() {
        FindFiles findFiles =  new FindFiles().relative(".");
        findFiles.setNameRegex("(.*\\.xml)").setRecursive(false);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "./pom.xml")));

        Assert.assertEquals(findFiles.getNameRegex(), "(.*\\.xml)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertFalse(findFiles.isRecursive());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder only (not including sub-folders)");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void pathRegexTest() {
        FindFiles findFiles =  new FindFiles().relative("src").setRecursive(false);
        findFiles.setNameRegex("(Same.*\\.java)").setPathRegex("(.*\\/java\\/.*)");
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 3);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/java/com/testapp/SamePackageOtherSuperclass.java")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/java/com/testapp/SamePackageSubclass.java")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/java/com/testapp/SamePackageSuperclass.java")));

        Assert.assertEquals(findFiles.getNameRegex(), "(Same.*\\.java)");
        Assert.assertEquals(findFiles.getPathRegex(), "(.*\\/java\\/.*)");
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under src and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void pathRegexNoFilesFoundTest() {
        FindFiles findFiles =  new FindFiles("(.*Impl\\.java)", "(.*\\/java\\/.*)");
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertNotNull(executionResult.getValue());
        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 0);
        Assert.assertEquals(findFiles.getNameRegex(), "(.*Impl\\.java)");
        Assert.assertEquals(findFiles.getPathRegex(), "(.*\\/java\\/.*)");
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getDetails(), "No files have been found");
    }

    @Test
    public void noFilesFoundTest() {
        FindFiles findFiles =  new FindFiles("(.*\\.txt)", true).relative("");
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertNotNull(executionResult.getValue());
        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 0);
        Assert.assertEquals(findFiles.getNameRegex(), "(.*\\.txt)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getDetails(), "No files have been found");
    }

}
