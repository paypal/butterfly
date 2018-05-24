package com.paypal.butterfly.utilities.conditions;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;

/**
 * Unit tests for {@link XmlXPathExists}
 *
 * @author mmcrockett
 */
public class XmlXPathExistsTest extends TransformationUtilityTestHelper {

    @Test
    public void returnsTrueForMatch() {
        XmlXPathExists xmlXPathExists = new XmlXPathExists().setXPathExpression("/project/artifactId/text()").relative("pom.xml");
        Assert.assertEquals(xmlXPathExists.getXPathExpressionString(), "/project/artifactId/text()");
        TUExecutionResult executionResult = xmlXPathExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getValue(), true);
        Assert.assertEquals(xmlXPathExists.getDescription(), "Check if xml xpath query /project/artifactId/text() exists in XML file pom.xml");
    }

    @Test
    public void returnsFalseOnNoMatch() {
        XmlXPathExists xmlXPathExists = new XmlXPathExists("/project/blahblah").relative("pom.xml");
        TUExecutionResult executionResult = xmlXPathExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());
        Assert.assertEquals(executionResult.getValue(), false);
        Assert.assertEquals(xmlXPathExists.getDescription(), "Check if xml xpath query /project/blahblah exists in XML file pom.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnNonStringXPath() {
        new XmlXPathExists(null).relative("pom.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnEmptyXPath() {
        new XmlXPathExists("").relative("pom.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnBadXPathSetup() {
        new XmlXPathExists("`").relative("pom.xml");
    }

    @Test
    public void reportsFailureOnBadXml() {
        XmlXPathExists xmlXPathExists = new XmlXPathExists("blah").relative("src/main/resources/dogs.yaml");
        TUExecutionResult executionResult = xmlXPathExists.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.ERROR);
        Assert.assertNull(executionResult.getValue());
        Assert.assertEquals(xmlXPathExists.getDescription(), "Check if xml xpath query blah exists in XML file src/main/resources/dogs.yaml");
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationUtilityException.class);
        Assert.assertEquals(executionResult.getException().getMessage(), "File content could not be parsed properly in XML format");
    }

}
