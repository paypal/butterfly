package com.paypal.butterfly.utilities.xml;

import static org.testng.Assert.*;

import org.testng.annotations.Test;
import org.xml.sax.SAXParseException;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

/**
 * Unit tests for {@link XmlXPathElement}
 *
 * @author facarvalho
 */
public class XmlXPathElementTest extends TransformationUtilityTestHelper {

    @Test
    public void valueTest() {
        final String XPATH_EXPRESSION = "//*[name()='jaxrs:server']/@address";
        XmlXPathElement xmlXPathElement = new XmlXPathElement(XPATH_EXPRESSION).relative("foo.xml");
        assertEquals(xmlXPathElement.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xmlXPathElement.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        assertNotNull(executionResult.getValue());
        assertEquals(executionResult.getValue(), "/blah");
        assertEquals(xmlXPathElement.getDescription(), "Retrieves data from XML file foo.xml based on XPath expression " + XPATH_EXPRESSION);
    }

    @Test
    public void nullTest() {
        final String XPATH_EXPRESSION = "/foo/bar";
        XmlXPathElement xmlXPathElement = new XmlXPathElement(XPATH_EXPRESSION).relative("foo.xml");
        assertEquals(xmlXPathElement.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xmlXPathElement.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.NULL);
        assertNull(executionResult.getValue());
        assertEquals(xmlXPathElement.getDescription(), "Retrieves data from XML file foo.xml based on XPath expression " + XPATH_EXPRESSION);
    }

    @Test
    public void invalidXmlFileTest() {
        final String XPATH_EXPRESSION = "/foo/bar";
        XmlXPathElement xmlXPathElement = new XmlXPathElement(XPATH_EXPRESSION).relative("src/main/resources/dogs.yaml");
        assertEquals(xmlXPathElement.getXPathExpressionString(), XPATH_EXPRESSION);
        TUExecutionResult executionResult = xmlXPathElement.execution(transformedAppFolder, transformationContext);
        assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        assertNull(executionResult.getValue());
        assertEquals(xmlXPathElement.getDescription(), "Retrieves data from XML file src/main/resources/dogs.yaml based on XPath expression " + XPATH_EXPRESSION);
        assertNotNull(executionResult.getException());
        assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        assertEquals(executionResult.getException().getMessage(), "File content could not be parsed properly in XML format");
        assertEquals(executionResult.getException().getCause().getClass(), SAXParseException.class);
    }

    @Test
    public void invalidXPathExpressionTest() {
        final String XPATH_EXPRESSION = "@#$123456";
        try {
            new XmlXPathElement().setXPathExpression(XPATH_EXPRESSION);
            fail("Exception was supposed to be thrown but was not");
        } catch (TransformationDefinitionException e) {
            assertEquals(e.getMessage(), "XPath expression '@#$123456' didn't compile correctly.");
        }
    }

}
