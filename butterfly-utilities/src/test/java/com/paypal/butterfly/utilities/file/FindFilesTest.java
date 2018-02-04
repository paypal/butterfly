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
    public void fileRecursiveOneFoundTest() {
        FindFiles findFiles =  new FindFiles("web.xml", true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/webapp/WEB-INF/web.xml")));

        Assert.assertEquals(findFiles.getNameRegex(), "web.xml");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertFalse(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileRecursiveMultipleFoundTest() {
        FindFiles findFiles =  new FindFiles("(.*\\.xml)", true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 7);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/pom.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/foo.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/webapp/WEB-INF/web.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/copy_of_web.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/no_parent_pom.xml")));

        Assert.assertEquals(findFiles.getNameRegex(), "(.*\\.xml)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertFalse(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileNonRecursiveOneFoundTest() {
        FindFiles findFiles =  new FindFiles().relative(".").setIncludeFolders(false).setIncludeFiles(true);
        findFiles.setNameRegex("pom.xml").setRecursive(false);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "./pom.xml")));

        Assert.assertEquals(findFiles.getNameRegex(), "pom.xml");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertFalse(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertFalse(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder only (not including sub-folders)");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileNonRecursiveMultipleFoundTest() {
        FindFiles findFiles =  new FindFiles().setIncludeFolders(false).setIncludeFiles(true);
        findFiles.setNameRegex("(.*\\.xml)").setRecursive(false);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 2);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "pom.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "foo.xml")));

        Assert.assertEquals(findFiles.getNameRegex(), "(.*\\.xml)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertFalse(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertFalse(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder only (not including sub-folders)");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void fileNoneFoundTest() {
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

    @Test
    public void filePathRegexTest() {
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
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertFalse(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under src and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void filePathRegexNoneFoundTest() {
        FindFiles findFiles =  new FindFiles("(.*Impl\\.java)", "(.*\\/java\\/.*)");
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertNotNull(executionResult.getValue());
        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 0);
        Assert.assertEquals(findFiles.getNameRegex(), "(.*Impl\\.java)");
        Assert.assertEquals(findFiles.getPathRegex(), "(.*\\/java\\/.*)");
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertFalse(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getDetails(), "No files have been found");
    }

    @Test
    public void folderRecursiveOneFoundTest() {
        FindFiles findFiles =  new FindFiles("resources", true).setIncludeFiles(false).setIncludeFolders(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources")));
        Assert.assertEquals(findFiles.getNameRegex(), "resources");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertFalse(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void folderRecursiveMultipleFoundTest() {
        FindFiles findFiles =  new FindFiles("(more_yaml|testapp)", true, false, true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 2);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/java/com/testapp")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/more_yaml")));

        Assert.assertEquals(findFiles.getNameRegex(), "(more_yaml|testapp)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertFalse(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void folderNonRecursiveOneFoundTest() {
        FindFiles findFiles =  new FindFiles("(sr.)", false).relative(".").setIncludeFolders(true).setIncludeFiles(false);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "./src")));

        Assert.assertEquals(findFiles.getNameRegex(), "(sr.)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertFalse(findFiles.isRecursive());
        Assert.assertFalse(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder only (not including sub-folders)");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void folderNonRecursiveMultipleFoundTest() {
        FindFiles findFiles =  new FindFiles().setRecursive(false).setIncludeFolders(true).setIncludeFiles(false);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 2);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "src")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "blah")));

        Assert.assertNull(findFiles.getNameRegex());
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertFalse(findFiles.isRecursive());
        Assert.assertFalse(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder only (not including sub-folders)");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void folderNoneFoundTest() {
        FindFiles findFiles =  new FindFiles("manga", true).setIncludeFiles(false).setIncludeFolders(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertNotNull(executionResult.getValue());
        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 0);
        Assert.assertEquals(findFiles.getNameRegex(), "manga");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertFalse(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getDetails(), "No files have been found");
    }

    @Test
    public void folderPathRegexTest() {
        FindFiles findFiles =  new FindFiles().setPathRegex("(.*\\/resources.*)").setRecursive(true);
        findFiles.setIncludeFiles(false).setIncludeFolders(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/more_yaml")));

        Assert.assertNull(findFiles.getNameRegex());
        Assert.assertEquals(findFiles.getPathRegex(), "(.*\\/resources.*)");
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertFalse(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void folderPathRegexNoneFoundTest() {
        FindFiles findFiles =  new FindFiles().setPathRegex("(.*\\/yaba.*)").setRecursive(true);
        findFiles.setIncludeFiles(false).setIncludeFolders(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertNotNull(executionResult.getValue());
        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 0);
        Assert.assertNull(findFiles.getNameRegex());
        Assert.assertEquals(findFiles.getPathRegex(), "(.*\\/yaba.*)");
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertFalse(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getDetails(), "No files have been found");
    }

    @Test
    public void bothRecursiveOneFoundTest() {
        FindFiles findFiles =  new FindFiles("web.xml", true).setIncludeFolders(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/webapp/WEB-INF/web.xml")));

        Assert.assertEquals(findFiles.getNameRegex(), "web.xml");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void bothRecursiveMultipleFoundTest() {
        FindFiles findFiles =  new FindFiles("(.*yaml)", true).setIncludeFolders(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 3);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/dogs.yaml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/more_yaml/dogs.yaml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/more_yaml")));

        Assert.assertEquals(findFiles.getNameRegex(), "(.*yaml)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void bothNonRecursiveOneFoundTest() {
        FindFiles findFiles =  new FindFiles("(bla.)", false).relative(".").setIncludeFolders(true).setIncludeFiles(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 1);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "./blah")));

        Assert.assertEquals(findFiles.getNameRegex(), "(bla.)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertFalse(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder only (not including sub-folders)");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void bothNonRecursiveMultipleFoundTest() {
        FindFiles findFiles =  new FindFiles().setRecursive(false).setIncludeFolders(true).setIncludeFiles(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 4);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "src")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "blah")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "foo.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "pom.xml")));

        Assert.assertNull(findFiles.getNameRegex());
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertFalse(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder only (not including sub-folders)");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void bothRecursiveAllFoundTest() {
        FindFiles findFiles =  new FindFiles().setRecursive(true).setIncludeFolders(true).setIncludeFiles(true);
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 33);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "src")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "blah")));

        Assert.assertNull(findFiles.getNameRegex());
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void bothPathRegexTest() {
        FindFiles findFiles =  new FindFiles().setRecursive(true).setIncludeFiles(true).setIncludeFolders(true);
        findFiles.setPathRegex("(.*\\/resources.*)");
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 10);

        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/more_yaml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/more_yaml/dogs.yaml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/more_yaml/testapp")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/application.properties")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/copy_of_web.xml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/dogs.yaml")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/MANIFEST.MF")));
        Assert.assertTrue(files.contains(new File(transformedAppFolder, "/src/main/resources/no_parent_pom.xml")));

        Assert.assertNull(findFiles.getNameRegex());
        Assert.assertEquals(findFiles.getPathRegex(), "(.*\\/resources.*)");
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void bothNoneFoundTest() {
        FindFiles findFiles =  new FindFiles("(.*casa.*)", true, true, true).relative("");
        TUExecutionResult executionResult = findFiles.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.WARNING);
        Assert.assertNotNull(executionResult.getValue());
        List<File> files = (List<File>) executionResult.getValue();
        Assert.assertEquals(files.size(), 0);
        Assert.assertEquals(findFiles.getNameRegex(), "(.*casa.*)");
        Assert.assertNull(findFiles.getPathRegex());
        Assert.assertTrue(findFiles.isRecursive());
        Assert.assertTrue(findFiles.isIncludeFiles());
        Assert.assertTrue(findFiles.isIncludeFolders());
        Assert.assertEquals(findFiles.getDescription(), "Find files whose name and/or path match regular expression and are under the root folder and sub-folders");
        Assert.assertNull(executionResult.getException());
        Assert.assertEquals(executionResult.getDetails(), "No files have been found");
    }

}
