package com.paypal.butterfly.utilities.operations.pom.stax;

import com.paypal.butterfly.utilities.operations.pom.stax.XmlIndentation;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;

public class XmlIndentationTest {

    @Test
    public void testSpaces4() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/spaces4.xml", "    ");
    }

    @Test
    public void testSpaces4Comment() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/spaces4comment.xml", "    ");
    }

    @Test
    public void testSpaces4XmlTag() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/spaces4xmltag.xml", "    ");
    }

    @Test
    public void testSpaces4XmlTagSameLine() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/spaces4xmltagsameline.xml", "    ");
    }

    @Test
    public void testSpaces4FirstInLine() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/spaces4firstinline.xml", null);
    }

    @Test
    public void testSpaces3() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/spaces3.xml", "   ");
    }

    @Test
    public void testTabs() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/tabs.xml", "\t");
    }

    @Test
    public void testNoIndent() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/noindent.xml", "");
    }

    @Test
    public void testSingleLine() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/singleline.xml", null);
    }

    @Test
    public void testNoElements() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/noelements.xml", null);
    }

    @Test
    public void testNoElementsXmlTag() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/noelementsxmltag.xml", null);
    }

    @Test
    public void testOneElement() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/oneelement.xml", "    ");
    }

    @Test
    public void testOneElementXmlTag() throws XMLStreamException, FileNotFoundException {
        test("/indentTests/oneelementxmltag.xml", "    ");
    }

    private void test(String filePath, String expectedIndentation) throws XMLStreamException, FileNotFoundException {
        String actualIndentation = XmlIndentation.getFirst(new File(this.getClass().getResource(filePath).getFile()));
        Assert.assertEquals(actualIndentation, expectedIndentation);
    }

}
