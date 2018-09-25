package com.paypal.butterfly.test;

import static com.paypal.butterfly.test.Assert.assertAbort;
import static com.paypal.butterfly.test.Assert.assertTransformation;

import java.io.File;

import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AssertTest {
    
    private static final File TEST_RESOURCES = new File("test-resources");

    @BeforeClass
    public void beforeClass() {

        // This is necessary because Git doesn't allow empty folders to be checked in
        File app3Dir3 = new File(TEST_RESOURCES, "app3/dir3");
        if (!app3Dir3.exists()) {
            app3Dir3.mkdir();
        }
    }

    @Test
    public void sameContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app2");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "File content is not equal: /file1.txt")
    public void rootDifferentFileContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app8");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "File missing: /file1.txt")
    public void rootMissingFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app10");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Unexpected file found: /extra_file.txt")
    public void rootExtraFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app9");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Folder missing: /dir3")
    public void rootMissingFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app7");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Unexpected folder found: /dir3")
    public void rootExtraFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app7");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "File content is not equal: /dir1/dir2/file2.txt")
    public void nonRootDifferentFileContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app5");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "File missing: /dir3/file3.txt")
    public void nonRootMissingFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app3");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Unexpected file found: /dir3/file3.txt")
    public void nonRootExtraFileTest() {
        File expected = new File(TEST_RESOURCES, "/app3");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Folder missing: /dir1/dir2")
    public void nonRootMissingFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app6");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Unexpected folder found: /dir1/dir2")
    public void nonRootExtraFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app6");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test
    public void assertAbortTest() {
        assertAbort(TEST_RESOURCES, JavaEEToSpringBoot.class, true, true, null, "This application does not have a root pom.xml file");
    }

    @Test
    public void semanticXmlComparisonTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app11");
        assertTransformation(expected, actual, true);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "File content is not equal: /dir1/pom.xml")
    public void binaryXmlComparisonTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app11");
        assertTransformation(expected, actual, false);
    }

}
