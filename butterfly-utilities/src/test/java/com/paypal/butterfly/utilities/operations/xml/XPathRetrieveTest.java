package com.paypal.butterfly.utilities.operations.xml;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.xml.xpath.XPathConstants;

import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

        Node node1 = nodes.item(0);
        Element elmt1 = (Element) node1;
        assertEquals(elmt1.getAttributeNode("name").getValue(), "DS_test1");
        assertEquals(elmt1.getElementsByTagName("minimum-pool-size").item(0).getChildNodes().item(0).getNodeValue(), "3");
        assertEquals(elmt1.getElementsByTagName("maximum-pool-size").item(0).getChildNodes().item(0).getNodeValue(), "25");
        assertEquals(elmt1.getElementsByTagName("connection-timeout").item(0).getChildNodes().item(0).getNodeValue(), "1");

        Node node2 = nodes.item(1);
        Element elmt2 = (Element) node2;
        assertEquals(elmt2.getAttributeNode("name").getValue(), "DS_test2");
        assertEquals(elmt2.getElementsByTagName("minimum-pool-size").item(0).getChildNodes().item(0).getNodeValue(), "3");
        assertEquals(elmt2.getElementsByTagName("maximum-pool-size").item(0).getChildNodes().item(0).getNodeValue(), "25");

        assertEquals(xpathRetrieve.getDescription(), "Retrieve the XML data based on the given XPath query " + XPATH_EXPRESSION +  " and XML file foo1.xml");
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
        assertEquals(xpathRetrieve.getDescription(), "Retrieve the XML data based on the given XPath query " + XPATH_EXPRESSION +  " and XML file foo1.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void returnTypeNotSupportedTest() {
        new XPathRetrieve("/deliverable-settings", XPathConstants.BOOLEAN);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void nullTypeNotSupportedTest() {
        new XPathRetrieve("/deliverable-settings", null);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void invalidXPathExpressionTest() {
        new XPathRetrieve("`", XPathConstants.NODESET);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void nullXPathExpressionTest() {
        new XPathRetrieve(null, XPathConstants.NODESET);
    }

}
