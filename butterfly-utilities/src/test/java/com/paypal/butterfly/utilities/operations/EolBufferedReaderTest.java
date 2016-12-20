package com.paypal.butterfly.utilities.operations;

import com.paypal.butterfly.utilities.operations.EolBufferedReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static com.paypal.butterfly.utilities.operations.EolBufferedReader.*;

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
            while ((currentLine = reader.readLineKeepEndEOL()) != null) {
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
            line1 = reader.readLineKeepEndEOL();
            line2 = reader.readLineKeepEndEOL();
            line3 = reader.readLineKeepEndEOL();
            line4 = reader.readLineKeepEndEOL();
            line5 = reader.readLineKeepEndEOL();

            Assert.assertFalse(startsWithEOL(line1));
            Assert.assertTrue(endsWithEOL(line1));
            Assert.assertEquals(removeEOL(line1), "line1");
            Assert.assertEquals(getStartEOL(line1), null);
            Assert.assertEquals(getEndEOL(line1), "\n");

            Assert.assertFalse(startsWithEOL(line2));
            Assert.assertTrue(endsWithEOL(line2));
            Assert.assertEquals(removeEOL(line2), "line2");
            Assert.assertEquals(getStartEOL(line2), null);
            Assert.assertEquals(getEndEOL(line2), "\r");

            Assert.assertFalse(startsWithEOL(line3));
            Assert.assertTrue(endsWithEOL(line3));
            Assert.assertEquals(removeEOL(line3), "line3");
            Assert.assertEquals(getStartEOL(line3), null);
            Assert.assertEquals(getEndEOL(line3), "\r\n");

            Assert.assertTrue(startsWithEOL(line4));
            Assert.assertTrue(endsWithEOL(line4));
            Assert.assertEquals(removeEOL(line4), "");
            Assert.assertEquals(getStartEOL(line4), "\n");
            Assert.assertEquals(getEndEOL(line4), "\n");

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
            while ((currentLine = reader.readLineKeepStartEOL()) != null) {
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
            line1 = reader.readLineKeepStartEOL();
            line2 = reader.readLineKeepStartEOL();
            line3 = reader.readLineKeepStartEOL();
            line4 = reader.readLineKeepStartEOL();
            line5 = reader.readLineKeepStartEOL();
            line6 = reader.readLineKeepStartEOL();

            Assert.assertFalse(startsWithEOL(line1));
            Assert.assertFalse(endsWithEOL(line1));
            Assert.assertEquals(removeEOL(line1), "line1");
            Assert.assertEquals(getStartEOL(line1), null);
            Assert.assertEquals(getEndEOL(line1), null);

            Assert.assertTrue(startsWithEOL(line2));
            Assert.assertFalse(endsWithEOL(line2));
            Assert.assertEquals(removeEOL(line2), "line2");
            Assert.assertEquals(getStartEOL(line2), "\n");
            Assert.assertEquals(getEndEOL(line2), null);

            Assert.assertTrue(startsWithEOL(line3));
            Assert.assertFalse(endsWithEOL(line3));
            Assert.assertEquals(removeEOL(line3), "line3");
            Assert.assertEquals(getStartEOL(line3), "\r");
            Assert.assertEquals(getEndEOL(line3), null);

            Assert.assertTrue(startsWithEOL(line4));
            Assert.assertTrue(endsWithEOL(line4));
            Assert.assertEquals(removeEOL(line4), "");
            Assert.assertEquals(getStartEOL(line4), "\r\n");
            Assert.assertEquals(getEndEOL(line4), "\r\n");


            Assert.assertTrue(startsWithEOL(line5));
            Assert.assertTrue(endsWithEOL(line5));
            Assert.assertEquals(removeEOL(line5), "");
            Assert.assertEquals(getStartEOL(line5), "\n");
            Assert.assertEquals(getEndEOL(line5), "\n");

            Assert.assertNull(line6);
        } finally {
            if(reader != null) reader.close();
        }
    }

}
