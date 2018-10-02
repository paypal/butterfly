package com.paypal.butterfly.utilities.xml;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import javax.xml.xpath.XPathConstants;

import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;


/**
 * Unit tests for {@link XmlXPathElement}
 *
 * @author spetratos
 */
public class XmlXPathRetrieveTest extends TransformationUtilityTestHelper {

    @Test
    public void retrieveTest() {
        final String XPATH_EXPRESSION = "/deliverable-settings/data-sources/data-source";
        XmlXPathRetrieve xpathRetrieve = new XmlXPathRetrieve(XPATH_EXPRESSION, XPathConstants.NODESET).relative("foo1.xml");
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
        XmlXPathRetrieve xpathRetrieve = new XmlXPathRetrieve(XPATH_EXPRESSION, XPathConstants.NODESET).relative("foo1.xml");
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
        new XmlXPathRetrieve("/deliverable-settings", XPathConstants.BOOLEAN);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void nullTypeNotSupportedTest() {
        new XmlXPathRetrieve("/deliverable-settings", null);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void emptyXPathExpressionTest() {
        new XmlXPathRetrieve("`", XPathConstants.NODESET);
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void nullXPathExpressionTest() {
        new XmlXPathRetrieve(null, XPathConstants.NODESET);
    }

    @Test
    public void valueTest() {
        final String XPATH_EXPRESSION = "//*[name()='jaxrs:server']/@address";
        XmlXPathRetrieve xmlXPathElement = new XmlXPathRetrieve(XPATH_EXPRESSION).relative("foo.xml");
        assertEquals(xmlXPathElement.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xmlXPathElement.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertNotNull(executionResult.getValue());
        assertEquals(executionResult.getValue(), "/blah");
        assertEquals(xmlXPathElement.getDescription(), "Retrieve the XML data based on the given XPath query " + XPATH_EXPRESSION +  " and XML file foo.xml");
    }

    @Test
    public void nullTest() {
        final String XPATH_EXPRESSION = "/foo/bar";
        XmlXPathRetrieve xmlXPathElement = new XmlXPathRetrieve(XPATH_EXPRESSION).relative("foo.xml");
        assertEquals(xmlXPathElement.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xmlXPathElement.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertNotNull(executionResult.getValue());
        assertEquals(xmlXPathElement.getDescription(), "Retrieve the XML data based on the given XPath query " + XPATH_EXPRESSION +  " and XML file foo.xml");
    }

    @Test
    public void invalidXmlFileTest() {
        final String XPATH_EXPRESSION = "/foo/bar";
        XmlXPathRetrieve xmlXPathElement = new XmlXPathRetrieve(XPATH_EXPRESSION).relative("src/main/resources/dogs.yaml");
        assertEquals(xmlXPathElement.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xmlXPathElement.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertNull(executionResult.getValue());
        assertNotNull(executionResult.getException());
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "File content could not be parsed properly in XML format");
        assertEquals(executionResult.getException().getCause().getClass(), SAXParseException.class);
    }

    @Test
    public void invalidXPathExpressionTest() {
        final String XPATH_EXPRESSION = "@#$123456";
        try {
            new XmlXPathRetrieve().setXPathExpression(XPATH_EXPRESSION);
            fail("Exception was supposed to be thrown but was not");
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "XPath expression '@#$123456' didn't compile correctly.");
        }
    }

}
