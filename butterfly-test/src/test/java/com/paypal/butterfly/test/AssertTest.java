package com.paypal.butterfly.test;

import static com.paypal.butterfly.test.Assert.assertAbort;
import static com.paypal.butterfly.test.Assert.assertTransformation;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;

import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(classes = ButterflyTestConfig.class)
public class AssertTest extends AbstractTestNGSpringContextTests {

    private static final File TEST_RESOURCES = new File("test-resources");

    // General error message header
    private static final String GEMH = "Baseline and transformed applications don't match, as detailed below:\n\n" +
            "\tBaseline application:    (.*)/butterfly/butterfly-test/test-resources/app[0-9]*\n" +
            "\tTransformed application: (.*)/butterfly/butterfly-test/test-resources/app[0-9]*\n\n\n\t";

    // Single file different error message header
    private static final String SFDEMH = "Baseline and transformed applications don't match, the contents of one file differ, as detailed below:\n\n" +
            "\tBaseline application:    (.*)/butterfly/butterfly-test/test-resources/app[0-9]*\n" +
            "\tTransformed application: (.*)/butterfly/butterfly-test/test-resources/app[0-9]*\n\n";

    @Autowired
    private ButterflyFacade facade;

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

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = SFDEMH + "\n\tFile: \t\t/file1.txt\n\tAt: \t\tline 1, column 1\n\tExpected: \t'f'\n\tFound: \t\t'b'")
    public void rootDifferentFileContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app8");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Missing in transformed application \\(1\\):\n\t\t/file1.txt\n")
    public void rootMissingFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app10");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Unexpectedly found in transformed application \\(1\\):\n\t\t/extra_file.txt\n")
    public void rootExtraFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app9");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Missing in transformed application \\(1\\):\n\t\t/dir3 <dir>\n")
    public void rootMissingFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app7");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Unexpectedly found in transformed application \\(1\\):\n\t\t/dir3 <dir>\n")
    public void rootExtraFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app7");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = SFDEMH + "\n\tFile: \t\t/dir1/dir2/file2.txt\n\tAt: \t\tline 1, column 5\n\tExpected: \t'2'\n\tFound: \t\t'0'")
    public void nonRootDifferentFileContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app5");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Missing in transformed application \\(1\\):\n\t\t/dir3/file3.txt\n")
    public void nonRootMissingFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app3");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Unexpectedly found in transformed application \\(1\\):\n\t\t/dir3/file3.txt\n")
    public void nonRootExtraFileTest() {
        File expected = new File(TEST_RESOURCES, "/app3");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Missing in transformed application \\(1\\):\n\t\t/dir1/dir2 <dir>\n")
    public void nonRootMissingFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app6");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = GEMH + "Unexpectedly found in transformed application \\(1\\):\n\t\t/dir1/dir2 <dir>\n")
    public void nonRootExtraFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app6");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test
    public void assertAbortTest() {
        assertAbort(facade, TEST_RESOURCES, JavaEEToSpringBoot.class, null, "This application does not have a root pom.xml file");
    }

    @Test
    public void semanticXmlComparisonTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app11");
        assertTransformation(expected, actual, true);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = SFDEMH + "\n\tFile: \t\t/dir1/pom.xml\n\tAt: \t\tline 3, column 3\n\tExpected: \t' '\n\tFound: \t\t'<'")
    public void binaryXmlComparisonTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app11");
        assertTransformation(expected, actual, false);
    }

    @Test
    public void multipleTest() {
        final String EXPECTED_EXCEPTION_MESSAGE =
                "Baseline and transformed applications don't match, as detailed below:\n\n" +
                        "\tBaseline application:    (.*)\\/butterfly\\/butterfly-test\\/test-resources\\/app[0-9]*\n" +
                        "\tTransformed application: (.*)\\/butterfly\\/butterfly-test\\/test-resources\\/app[0-9]*\n\n\n\t" +
                "Missing in transformed application \\(7\\):\n" +
                    "\t\t\\/dir1\\/dir13 <dir>\n" +
                    "\t\t\\/dir1\\/pom\\.xml\n" +
                    "\t\t\\/dir7 <dir>\n" +
                    "\t\t\\/dir8 <dir>\n" +
                    "\t\t\\/dir9 <dir>\n" +
                    "\t\t\\/file1\\.txt\n" +
                    "\t\t\\/file2\\.txt\n" +
                "\n\tUnexpectedly found in transformed application \\(6\\):\n" +
                    "\t\t\\/dir1\\/dir2\\/dir3 <dir>\n" +
                    "\t\t\\/dir1\\/dir2\\/dir5 <dir>\n" +
                    "\t\t\\/dir3\\/file5\\.txt\n" +
                    "\t\t\\/dir3\\/file7\\.txt\n" +
                    "\t\t\\/foo\\.txt\n" +
                    "\t\t\\/pom\\.xml\n" +
                "\n\tDifferent file content \\(3\\):\n" +
                    "\t\t\\/dir3\\/file3\\.txt\n" +
                    "\t\t\\/dir3\\/fileb\\.txt\n" +
                    "\t\t\\/dir3\\/filec\\.txt\n";

        File expected = new File(TEST_RESOURCES, "/app12");
        File actual = new File(TEST_RESOURCES, "/app13");

        try {
            assertTransformation(expected, actual, false);
            fail("Expected exception was not thrown");
        } catch (AssertionError e) {
            assertTrue(e.getMessage().matches(EXPECTED_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void multipleManyEntriesTest() {
        final String EXPECTED_EXCEPTION_MESSAGE =
                "Baseline and transformed applications don't match, as detailed below:\n\n" +
                    "\tBaseline application:    (.*)\\/butterfly\\/butterfly-test\\/test-resources\\/app[0-9]*\n" +
                    "\tTransformed application: (.*)\\/butterfly\\/butterfly-test\\/test-resources\\/app[0-9]*\n\n\n\t" +
                "Missing in transformed application \\(10\\):\n" +
                    "\t\t\\/folderA <dir>\n" +
                    "\t\t\\/folderB <dir>\n" +
                    "\t\t\\/folderC <dir>\n" +
                    "\t\t\\/folderD <dir>\n" +
                    "\t\t\\/folderE <dir>\n" +
                    "\t\t\\/folderF <dir>\n" +
                    "\t\t\\/folderG <dir>\n" +
                    "\t\t\\/folderH <dir>\n" +
                    "\t\t\\/folderI <dir>\n" +
                    "\t\t\\/folderJ <dir>\n" +
                "\n\tUnexpectedly found in transformed application \\(11\\):\n" +
                    "\t\t\\/dirA <dir>\n" +
                    "\t\t\\/dirB <dir>\n" +
                    "\t\t\\/dirC <dir>\n" +
                    "\t\t\\/dirD <dir>\n" +
                    "\t\t\\/dirE <dir>\n" +
                    "\t\t\\/fileA\\.txt\n" +
                    "\t\t\\/fileB\\.txt\n" +
                    "\t\t\\/fileC\\.txt\n" +
                    "\t\t\\/fileD\\.txt\n" +
                    "\t\t\\/fileE\\.txt\n" +
                    "\t\t\\(More 1\\)\n" +
                "\n\tDifferent file content \\(1\\):\n" +
                        "\t\t\\/dir3\\/file3\\.txt\n";

        File expected = new File(TEST_RESOURCES, "/app14");
        File actual = new File(TEST_RESOURCES, "/app15");

        try {
            assertTransformation(expected, actual, false);
            fail("Expected exception was not thrown");
        } catch (AssertionError e) {
            assertTrue(e.getMessage().matches(EXPECTED_EXCEPTION_MESSAGE));
        }
    }

}
