package com.paypal.butterfly.utilities.xml;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;

public class XmlIndentationStaticTest {

    @Test
    public void testSpaces4() throws XMLStreamException, FileNotFoundException {
        test("spaces4.xml", "    ");
    }

    @Test
    public void testSpaces4Comment() throws XMLStreamException, FileNotFoundException {
        test("spaces4comment.xml", "    ");
    }

    @Test
    public void testSpaces4XmlTag() throws XMLStreamException, FileNotFoundException {
        test("spaces4xmltag.xml", "    ");
    }

    @Test
    public void testSpaces4XmlTagSameLine() throws XMLStreamException, FileNotFoundException {
        test("spaces4xmltagsameline.xml", "    ");
    }

    @Test
    public void testSpaces4FirstInLine() throws XMLStreamException, FileNotFoundException {
        test("spaces4firstinline.xml", null);
    }

    @Test
    public void testSpaces3() throws XMLStreamException, FileNotFoundException {
        test("spaces3.xml", "   ");
    }

    @Test
    public void testTabs() throws XMLStreamException, FileNotFoundException {
        test("tabs.xml", "\t");
    }

    @Test
    public void testNoIndent() throws XMLStreamException, FileNotFoundException {
        test("noindent.xml", "");
    }

    @Test
    public void testSingleLine() throws XMLStreamException, FileNotFoundException {
        test("singleline.xml", null);
    }

    @Test
    public void testNoElements() throws XMLStreamException, FileNotFoundException {
        test("noelements.xml", null);
    }

    @Test
    public void testNoElementsXmlTag() throws XMLStreamException, FileNotFoundException {
        test("noelementsxmltag.xml", null);
    }

    @Test
    public void testOneElement() throws XMLStreamException, FileNotFoundException {
        test("oneelement.xml", "    ");
    }

    @Test
    public void testOneElementXmlTag() throws XMLStreamException, FileNotFoundException {
        test("oneelementxmltag.xml", "    ");
    }

    private void test(String filePath, String expectedIndentation) throws XMLStreamException, FileNotFoundException {
        String actualIndentation = XmlIndentation.getFirst(new File(this.getClass().getResource("/test-app/indentTests/" + filePath).getFile()));
        Assert.assertEquals(actualIndentation, expectedIndentation);
    }

}
