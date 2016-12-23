package com.paypal.butterfly.utilities.operations;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Unit test for {@link EolHelper}
 *
 * @author facarvalho
 */
public class EolHelperTest {

    private static final String LINE1 = "blah\n";
    private static final String LINE2 = "\r\nblah";
    private static final String LINE3 = "blah";

    @Test
    public void findEolTest() throws IOException, URISyntaxException {
        File file = new File(EolHelperTest.class.getResource("/billy.yaml").toURI());
        Assert.assertEquals(EolHelper.findEol(file), "\n");
    }

    @Test
    public void findEolDefaultToOsTest() throws IOException, URISyntaxException {
        File file = new File(EolHelperTest.class.getResource("/billy.yaml").toURI());
        Assert.assertEquals(EolHelper.findEolDefaultToOs(file), "\n");

        file = new File(EolHelperTest.class.getResource("/oneLineFile.txt").toURI());
        Assert.assertEquals(EolHelper.findEolDefaultToOs(file), "\n");
    }

    @Test
    public void removeEolTest() {
        Assert.assertEquals(EolHelper.removeEol(LINE1), "blah");
        Assert.assertEquals(EolHelper.removeEol(LINE2), "blah");
        Assert.assertEquals(EolHelper.removeEol(LINE3), "blah");
    }

    @Test
    public void startsWithEolTest() {
        Assert.assertFalse(EolHelper.startsWithEol(LINE1));
        Assert.assertTrue(EolHelper.startsWithEol(LINE2));
        Assert.assertFalse(EolHelper.startsWithEol(LINE3));
    }

    @Test
    public void endsWithEolTest() {
        Assert.assertTrue(EolHelper.endsWithEol(LINE1));
        Assert.assertFalse(EolHelper.endsWithEol(LINE2));
        Assert.assertFalse(EolHelper.endsWithEol(LINE3));
    }

    @Test
    public void getStartEolTest() {
        Assert.assertEquals(EolHelper.getStartEol(LINE1), null);
        Assert.assertEquals(EolHelper.getStartEol(LINE2), "\r\n");
        Assert.assertEquals(EolHelper.getStartEol(LINE3), null);
    }

    @Test
    public void getEndEolTest() {
        Assert.assertEquals(EolHelper.getEndEol(LINE1), "\n");
        Assert.assertEquals(EolHelper.getEndEol(LINE2), null);
        Assert.assertEquals(EolHelper.getEndEol(LINE3), null);
    }
    
}
