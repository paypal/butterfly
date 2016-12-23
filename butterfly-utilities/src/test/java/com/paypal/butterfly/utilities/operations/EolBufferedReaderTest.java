package com.paypal.butterfly.utilities.operations;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static com.paypal.butterfly.utilities.operations.EolHelper.*;

/**
 * Unit test for {@link EolBufferedReader}
 *
 * @author facarvalho
 */
public class EolBufferedReaderTest {

    //line1\n
    //line2\r
    //line3\r\n
    //\n
    //
    private static final String TEST_STRING = "line1\nline2\rline3\r\n\n";

    @Test
    public void readLineKeepEndEOLBytesCountTest() throws IOException {
        EolBufferedReader reader = null;
        try {
            reader = new EolBufferedReader(new BufferedReader(new StringReader(TEST_STRING)));
            String currentLine;
            int bytesCount = 0;
            while ((currentLine = reader.readLineKeepEol()) != null) {
                bytesCount += currentLine.length();
            }
            Assert.assertEquals(TEST_STRING.length(), bytesCount);
        } finally {
            if(reader != null) reader.close();
        }
    }

    @Test
    public void readLineKeepEndEOLTest() throws IOException {
        EolBufferedReader reader = null;
        try {
            reader = new EolBufferedReader(new BufferedReader(new StringReader(TEST_STRING)));

            String line1, line2, line3, line4, line5;
            line1 = reader.readLineKeepEol();
            line2 = reader.readLineKeepEol();
            line3 = reader.readLineKeepEol();
            line4 = reader.readLineKeepEol();
            line5 = reader.readLineKeepEol();

            Assert.assertFalse(startsWithEol(line1));
            Assert.assertTrue(endsWithEol(line1));
            Assert.assertEquals(removeEol(line1), "line1");
            Assert.assertEquals(getStartEol(line1), null);
            Assert.assertEquals(getEndEol(line1), "\n");

            Assert.assertFalse(startsWithEol(line2));
            Assert.assertTrue(endsWithEol(line2));
            Assert.assertEquals(removeEol(line2), "line2");
            Assert.assertEquals(getStartEol(line2), null);
            Assert.assertEquals(getEndEol(line2), "\r");

            Assert.assertFalse(startsWithEol(line3));
            Assert.assertTrue(endsWithEol(line3));
            Assert.assertEquals(removeEol(line3), "line3");
            Assert.assertEquals(getStartEol(line3), null);
            Assert.assertEquals(getEndEol(line3), "\r\n");

            Assert.assertTrue(startsWithEol(line4));
            Assert.assertTrue(endsWithEol(line4));
            Assert.assertEquals(removeEol(line4), "");
            Assert.assertEquals(getStartEol(line4), "\n");
            Assert.assertEquals(getEndEol(line4), "\n");

            Assert.assertNull(line5);
        } finally {
            if(reader != null) reader.close();
        }
    }

    @Test
    public void readLineKeepStartEOLBytesCountTest() throws IOException {
        EolBufferedReader reader = null;
        try {
            reader = new EolBufferedReader(new BufferedReader(new StringReader(TEST_STRING)));
            String currentLine;
            int bytesCount = 0;
            while ((currentLine = reader.readLineKeepStartEol()) != null) {
                bytesCount += currentLine.length();
            }
            Assert.assertEquals(TEST_STRING.length(), bytesCount);
        } finally {
            if(reader != null) reader.close();
        }
    }

    @Test
    public void readLineKeepStartEOLTest() throws IOException {
        EolBufferedReader reader = null;
        try {
            reader = new EolBufferedReader(new BufferedReader(new StringReader(TEST_STRING)));

            String line1, line2, line3, line4, line5, line6;
            line1 = reader.readLineKeepStartEol();
            line2 = reader.readLineKeepStartEol();
            line3 = reader.readLineKeepStartEol();
            line4 = reader.readLineKeepStartEol();
            line5 = reader.readLineKeepStartEol();
            line6 = reader.readLineKeepStartEol();

            Assert.assertFalse(startsWithEol(line1));
            Assert.assertFalse(endsWithEol(line1));
            Assert.assertEquals(removeEol(line1), "line1");
            Assert.assertEquals(getStartEol(line1), null);
            Assert.assertEquals(getEndEol(line1), null);

            Assert.assertTrue(startsWithEol(line2));
            Assert.assertFalse(endsWithEol(line2));
            Assert.assertEquals(removeEol(line2), "line2");
            Assert.assertEquals(getStartEol(line2), "\n");
            Assert.assertEquals(getEndEol(line2), null);

            Assert.assertTrue(startsWithEol(line3));
            Assert.assertFalse(endsWithEol(line3));
            Assert.assertEquals(removeEol(line3), "line3");
            Assert.assertEquals(getStartEol(line3), "\r");
            Assert.assertEquals(getEndEol(line3), null);

            Assert.assertTrue(startsWithEol(line4));
            Assert.assertTrue(endsWithEol(line4));
            Assert.assertEquals(removeEol(line4), "");
            Assert.assertEquals(getStartEol(line4), "\r\n");
            Assert.assertEquals(getEndEol(line4), "\r\n");


            Assert.assertTrue(startsWithEol(line5));
            Assert.assertTrue(endsWithEol(line5));
            Assert.assertEquals(removeEol(line5), "");
            Assert.assertEquals(getStartEol(line5), "\n");
            Assert.assertEquals(getEndEol(line5), "\n");

            Assert.assertNull(line6);
        } finally {
            if(reader != null) reader.close();
        }
    }

}
