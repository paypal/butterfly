package com.paypal.butterfly.test;

import static com.paypal.butterfly.test.Assert.assertTransformation;

import java.io.File;

import org.testng.annotations.Test;

public class AssertTest {
    
    private static final File TEST_RESOURCES = new File(".", "test-resources");

    @Test
    public void sameContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app2");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "File missing: /dir3/file3.txt")
    public void missingFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app3");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Unexpected file found: /dir3/file3.txt")
    public void extraFileTest1() {
        File expected = new File(TEST_RESOURCES, "/app3");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Unexpected file found: /dir1/file5.txt")
    public void extraFileTest2() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app4");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "File content is not equal: /dir1/dir2/file2.txt")
    public void differentFileContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app5");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Folder missing: /dir1/dir2")
    public void missingFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app6");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Unexpected folder found: /dir1/dir2")
    public void extraFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app6");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

}
