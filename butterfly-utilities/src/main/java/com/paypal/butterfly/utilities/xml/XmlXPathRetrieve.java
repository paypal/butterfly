package com.paypal.butterfly.utilities.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

/**
 * Retrieves data from an XML file by using XPath expressions. If no element is
 * found, an empty string is returned if the return data type is
 * {@link XPathConstants.STRING}; if the return data type is
 * {@link XPathConstants.NODESET}, an empty node list is returned. If the XPath
 * expression won't compile, an error is returned. <br>
 * If the file is not a well formed XML file, an error is returned.
 *
 * @author spetratos
 */
public class XmlXPathRetrieve extends TransformationUtility<XmlXPathRetrieve> {

    private String xpathExpressionString;
    private XPathExpression xpathExpression;
    private QName returnDataType;

    private static final String DESCRIPTION = "Retrieve the XML data based on the given XPath query %s and XML file %s";

    /**
     * Retrieves data from an XML file by using XPath expressions. If no element is
     * found, an empty string is returned if the return data type is
     * {@link XPathConstants.STRING}; if the return data type is
     * {@link XPathConstants.NODESET}, an empty node list is returned.<br>
     * If the XPath expression won't compile, an error is returned.
     */
    public XmlXPathRetrieve() {
    }

    /**
     * Retrieves data from an XML file by using XPath expressions. If no element is
     * found, an empty string is returned if the return data type is
     * {@link XPathConstants.STRING}; if the return data type is
     * {@link XPathConstants.NODESET}, an empty node list is returned.<br>
     * If the XPath expression won't compile, an error is returned. <br>
     * If the file is not a well formed XML file, an error is returned.
     *
     * @param xpathExpressionString
     *            a string that compiles into a
     *            {@link javax.xml.xpath.XPathExpression}
     */
    public XmlXPathRetrieve(String xpathExpressionString) {
        setXPathExpression(xpathExpressionString, XPathConstants.STRING);
    }

    /**
     * Retrieves data from an XML file by using XPath expressions. If no element is
     * found, an empty string is returned if the return data type is
     * {@link XPathConstants.STRING}; if the return data type is
     * {@link XPathConstants.NODESET}, an empty node list is returned.<br>
     * If the XPath expression won't compile, an error is returned. <br>
     * If the XPath expression won't compile, an error is returned. <br>
     * If the file is not a well formed XML file, an error is returned.
     *
     * @param xpathExpressionString
     *            a string that compiles into a
     *            {@link javax.xml.xpath.XPathExpression}
     * 
     * @param returnDataType
     *            the XPath return type {@link XPathConstants}
     */
    public XmlXPathRetrieve(String xpathExpressionString, QName returnDataType) {
        setXPathExpression(xpathExpressionString, returnDataType);
    }

    /**
     * The {@link XPathExpression} to be used to retrieve the data from XML file.
     * 
     * @param xpathExpressionString
     *            a string that compiles into a
     *            {@link javax.xml.xpath.XPathExpression}
     * 
     * @param returnDataType
     *            The desired return type of the data retrieved
     *
     * @return this utility instance
     */
    public XmlXPathRetrieve setXPathExpression(String xpathExpressionString) {
        setXPathExpression(xpathExpressionString, returnDataType);
        return this;
    }

    /**
     * The {@link XPathExpression} to be used to retrieve the data from XML file.
     * Check if value is a blank String, if it is, then a
     * {@link TransformationDefinitionException} is thrown. <br>
     * 
     * @param xpathExpressionString
     *            a string that compiles into a
     *            {@link javax.xml.xpath.XPathExpression}
     * 
     * @param returnDataType
     *            The desired return type of the data retrieved
     *
     * @return this utility instance
     */
    public XmlXPathRetrieve setXPathExpression(String xpathExpressionString, QName returnDataType) {
        checkForBlankString("XPath Expression", xpathExpressionString);
        this.xpathExpression = checkXPathCompile(xpathExpressionString);
        if (returnDataType == null || !isReturnTypeValid(returnDataType)) {
            throw new TransformationDefinitionException("XPath data return type '" + returnDataType + "' is not valid.");
        }
        this.xpathExpressionString = xpathExpressionString;
        this.returnDataType = returnDataType;
        return this;
    }

    /**
     * Return the XPath expression
     *
     * @return the XPath expression
     */
    public String getXPathExpressionString() {
        return xpathExpressionString;
    }

    /**
     * Returns the XPatch return type
     * 
     * @return the returnDataType
     */
    public QName getReturnDataType() {
        return returnDataType;
    }

    /**
     * Sets the XPatch return type
     * 
     * @param returnDataType
     *            the returnDataType to set
     */
    public void setReturnDataType(QName returnDataType) {
        this.returnDataType = returnDataType;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, xpathExpressionString, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File xmlFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        TUExecutionResult result;
        Object xpathResult = null;

        try {
            if (xpathExpression == null) {
                result = TUExecutionResult.error(this, new TransformationUtilityException("XPathExpression was not specified"));
            } else {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(xmlFile);
                xpathResult = xpathExpression.evaluate(doc, returnDataType);
                result = TUExecutionResult.value(this, xpathResult);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            result = TUExecutionResult.error(this, new TransformationUtilityException("File content could not be parsed properly in XML format", e));
        } catch (XPathExpressionException e) {
            result = TUExecutionResult.error(this, new TransformationUtilityException("XPathExpression could not be evaluated correctly", e));
        }

        return result;
    }

    private XPathExpression checkXPathCompile(String expression) throws TransformationDefinitionException {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = null;

        try {
            expr = xpath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new TransformationDefinitionException("XPath expression '" + expression + "' didn't compile correctly.", e);
        }

        return expr;
    }

    /**
     * Verifies that the XPatch data type to return in valid
     * 
     * @param returnType
     *            the return type
     * 
     * @return a boolean
     */
    private boolean isReturnTypeValid(QName returnType) {
        if ((returnType.equals(XPathConstants.STRING)) || (returnType.equals(XPathConstants.NODESET))) {
            return true;
        }
        return false;
    }
}
