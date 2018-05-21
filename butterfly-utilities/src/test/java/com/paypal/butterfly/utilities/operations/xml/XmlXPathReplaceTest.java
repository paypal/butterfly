package com.paypal.butterfly.utilities.operations.xml;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Unit tests for {@link XmlXPathReplace}
 *
 * @author mmcrockett
 */
public class XmlXPathReplaceTest extends TransformationUtilityTestHelper {
    @Test
    public void changesTextValueOnMatch() throws IOException, XmlPullParserException {
        String xpath = "/project/artifactId";
        String replacement = "notfoo";
        XmlXPathReplace xmlElement = new XmlXPathReplace(xpath, replacement)
                .relative("pom.xml");
        TOExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        Assert.assertNotNull(executionResult.getDetails());
        assertChangedFile("pom.xml");
        Model pomModel = getTransformedPomModel("pom.xml");
        Assert.assertEquals(pomModel.getArtifactId(), replacement);
        Assert.assertEquals(xmlElement.getDescription(),
                "Replace text of xpath " + xpath + " in XML file pom.xml with " + replacement);
    }

    @Test
    public void changesElementValueOnMatch() throws IOException, XmlPullParserException, ParserConfigurationException {
        String xpath = "/project/dependencies/dependency[groupId='xmlunit']";

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element replacement = doc.createElement("dependency");
        Element groupId = doc.createElement("groupId");
        Element artifactId = doc.createElement("artifactId");
        Element version = doc.createElement("version");
        groupId.setTextContent("thegroup");
        artifactId.setTextContent("theartifact");
        version.setTextContent("theversion");
        replacement.appendChild(groupId);
        replacement.appendChild(artifactId);
        replacement.appendChild(version);

        XmlXPathReplace xmlElement = new XmlXPathReplace(xpath, replacement)
                .relative("pom.xml");
        TOExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.SUCCESS);
        Assert.assertNotNull(executionResult.getDetails());
        assertChangedFile("pom.xml");
        Model pomModel = getTransformedPomModel("pom.xml");
        boolean hasNewElement = false;

        for (Dependency dependency : pomModel.getDependencies()) {
            if (!hasNewElement && groupId.getTextContent().equals(dependency.getGroupId())) {
                Assert.assertEquals(dependency.getArtifactId(), artifactId.getTextContent());
                Assert.assertEquals(dependency.getVersion(), version.getTextContent());
                hasNewElement = true;
            } else {
                Assert.assertNotEquals(dependency.getGroupId(), "xmlunit");
                Assert.assertNotEquals(dependency.getArtifactId(), "xmlunit");
                Assert.assertNotEquals(dependency.getVersion(), "1.5");
            }
        }
        
        Assert.assertTrue(hasNewElement, "New element wasn't found in xml.");
        Assert.assertEquals(xmlElement.getDescription(),
                "Replace node of xpath " + xpath + " in XML file pom.xml with user supplied XML Element");
    }

    @Test
    public void returnsNoopOnNoMatch() {
        XmlXPathReplace xmlElement = new XmlXPathReplace("/project/blahblah", "test").relative("pom.xml");
        TOExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.NO_OP);
        Assert.assertNotNull(executionResult.getDetails());
        Assert.assertEquals(executionResult.getDetails(),
                "File pom.xml has had 0 node(s) where value replacement was applied based on xml xpath expression '/project/blahblah'");
        Assert.assertEquals(xmlElement.getDescription(),
                "Replace text of xpath /project/blahblah in XML file pom.xml with test");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnNonStringXPath() {
        new XmlXPathReplace(null, "test").relative("pom.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnEmptyXPath() {
        new XmlXPathReplace("", "test").relative("pom.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnBadXPathSetup() {
        new XmlXPathReplace("`", "test").relative("pom.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnNullStringReplacement() {
        String test = null;
        new XmlXPathReplace("/project/build/plugins/plugin/configuration/appPackages/cronusPackage/name/text()", test)
                .relative("pom.xml");
    }

    @Test(expectedExceptions = TransformationDefinitionException.class)
    public void reportsFailureOnNullElementReplacement() {
        Element test = null;
        new XmlXPathReplace("/project/build/plugins/plugin/configuration/appPackages/cronusPackage/name/text()", test)
                .relative("pom.xml");
    }

    @Test
    public void reportsFailureOnBadXml() {
        XmlXPathReplace xmlElement = new XmlXPathReplace("blah", "test")
                .relative("src/main/resources/couchdb.properties");
        TOExecutionResult executionResult = xmlElement.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TOExecutionResult.Type.ERROR);
        Assert.assertNotNull(executionResult.getException());
        Assert.assertEquals(executionResult.getException().getClass(), TransformationOperationException.class);
        Assert.assertEquals(executionResult.getException().getMessage(),
                "File content could not be parsed properly in XML format");
    }

}
