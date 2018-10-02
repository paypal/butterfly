package com.paypal.butterfly.utilities.operations.xml;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.xml.xpath.XPathConstants;

import org.testng.annotations.Test;
import org.w3c.dom.NodeList;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;


/**
 * Unit tests for {@link XmlXPathElement}
 *
 * @author spetratos
 */
public class XPathRetrieveTest extends TransformationUtilityTestHelper {

    @Test
    public void retrieveTest() {
        final String XPATH_EXPRESSION = "/deliverable-settings/data-sources/data-source";
        XPathRetrieve xpathRetrieve = new XPathRetrieve(XPATH_EXPRESSION, XPathConstants.NODESET).relative("foo1.xml");
        assertEquals(xpathRetrieve.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xpathRetrieve.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Object result = executionResult.getValue();
        NodeList nodes = (NodeList) result;
        assertNotNull(executionResult.getValue());
        assertEquals(nodes.getLength(), 2);
        assertEquals(xpathRetrieve.getDescription(), "Retriveve the XML XPath query " + XPATH_EXPRESSION +  " if it exists in XML file foo1.xml");
    }

    @Test
    public void notFoundTest() {
        final String XPATH_EXPRESSION = "/test";
        XPathRetrieve xpathRetrieve = new XPathRetrieve(XPATH_EXPRESSION, XPathConstants.NODESET).relative("foo1.xml");
        assertEquals(xpathRetrieve.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xpathRetrieve.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Object result = executionResult.getValue();
        NodeList nodes = (NodeList) result;
        assertNotNull(executionResult.getValue());
        assertEquals(nodes.getLength(), 0);
        assertEquals(xpathRetrieve.getDescription(), "Retriveve the XML XPath query " + XPATH_EXPRESSION +  " if it exists in XML file foo1.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void returnTypeNotSupportedTest() {
        new XPathRetrieve("/deliverable-settings", XPathConstants.BOOLEAN).relative("foo1.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void nullTypeNotSupportedTest() {
        new XPathRetrieve("/deliverable-settings", null).relative("foo1.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void invalidXPathExpressionTest() {
        new XPathRetrieve("`", XPathConstants.NODESET).relative("foo1.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void nullXPathExpressionTest() {
        new XPathRetrieve(null, XPathConstants.NODESET).relative("foo1.xml");
    }

}
