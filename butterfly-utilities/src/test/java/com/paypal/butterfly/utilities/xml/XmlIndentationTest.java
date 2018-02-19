package com.paypal.butterfly.utilities.xml;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class XmlIndentationTest extends TransformationUtilityTestHelper {

    @Test
    public void testSpaces4() {
        test("/indentTests/spaces4.xml", "    ");
    }

    @Test
    public void testSpaces4Comment() {
        test("/indentTests/spaces4comment.xml", "    ");
    }

    @Test
    public void testSpaces4XmlTag() {
        test("/indentTests/spaces4xmltag.xml", "    ");
    }

    @Test
    public void testSpaces4XmlTagSameLine() {
        test("/indentTests/spaces4xmltagsameline.xml", "    ");
    }

    @Test
    public void testSpaces4FirstInLine() {
        test("/indentTests/spaces4firstinline.xml", null);
    }

    @Test
    public void testSpaces3() {
        test("/indentTests/spaces3.xml", "   ");
    }

    @Test
    public void testTabs() {
        test("/indentTests/tabs.xml", "\t");
    }

    @Test
    public void testNoIndent() {
        test("/indentTests/noindent.xml", "");
    }

    @Test
    public void testSingleLine() {
        test("/indentTests/singleline.xml", null);
    }

    @Test
    public void testNoElements() {
        test("/indentTests/noelements.xml", null);
    }

    @Test
    public void testNoElementsXmlTag() {
        test("/indentTests/noelementsxmltag.xml", null);
    }

    @Test
    public void testOneElement() {
        test("/indentTests/oneelement.xml", "    ");
    }

    @Test
    public void testOneElementXmlTag() {
        test("/indentTests/oneelementxmltag.xml", "    ");
    }

    private void test(String filePath, String expectedIndentation) {
        XmlIndentation xmlIndentation = new XmlIndentation().relative(filePath);
        TUExecutionResult executionResult = xmlIndentation.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), (expectedIndentation == null ? TUExecutionResult.Type.NULL : TUExecutionResult.Type.VALUE));

        String actualIndentation = (String) executionResult.getValue();
        Assert.assertEquals(actualIndentation, expectedIndentation);
        Assert.assertEquals(xmlIndentation.getDescription(), "Returns the indentation used in XML file " + filePath);
    }

    @Test
    public void notExistentFileTest() {
        XmlIndentation xmlIndentation = new XmlIndentation().relative("cake.xml");
        TUExecutionResult executionResult = xmlIndentation.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File cake.xml does not exist");
        Assert.assertNull(executionResult.getValue());
    }

    @Test
    public void notXmlFileTest() {
        XmlIndentation xmlIndentation = new XmlIndentation().relative("blah/bli");
        TUExecutionResult executionResult = xmlIndentation.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File blah/bli is not recognized as a valid XML file");
        Assert.assertNull(executionResult.getValue());
    }

}
