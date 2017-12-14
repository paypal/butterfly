package com.paypal.butterfly.utilities.xml;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link XmlElement}
 *
 * @author facarvalho
 */
public class XmlElementTest extends TransformationUtilityTestHelper {

    @Test
    public void elementTest() {
        XmlElement xmlElement = new XmlElement("project.modelVersion").relative("pom.xml");
        TUExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getValue(), "4.0.0");
        Assert.assertEquals(xmlElement.getDescription(), "Retrieve the value of element project.modelVersion in XML file pom.xml");
    }

    @Test
    public void attributeTest() {
        XmlElement xmlElement = new XmlElement().setXmlElement("project").setAttribute("xmlns").relative("pom.xml");
        TUExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getValue(), "http://maven.apache.org/POM/4.0.0");
        Assert.assertEquals(xmlElement.getDescription(), "Retrieve the value of element project in XML file pom.xml");
    }

    @Test
    public void inexistentElementTest() {
        XmlElement xmlElement = new XmlElement("project.blah").relative("pom.xml");
        TUExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.NULL);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(xmlElement.getDescription(), "Retrieve the value of element project.blah in XML file pom.xml");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void inexistentAttributeTest() {
        XmlElement xmlElement = new XmlElement("project").setAttribute("blah").relative("pom.xml");
        TUExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.NULL);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(xmlElement.getDescription(), "Retrieve the value of element project in XML file pom.xml");
        Assert.assertNull(executionResult.getException());
    }

    @Test
    public void noXmlTest() {
        XmlElement xmlElement = new XmlElement().setXmlElement("project").relative("src/main/resources/dogs.yaml");
        TUExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(xmlElement.getDescription(), "Retrieve the value of element project in XML file src/main/resources/dogs.yaml");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File content could not be parsed properly in XML format");
    }

}
