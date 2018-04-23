package com.paypal.butterfly.utilities.conditions;

import java.io.File;
import java.io.IOException;

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

import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

/**
 * Checks if a particular xpath exists in an XML file.
 * <br>
 * If the xpath expression won't compile, an error is returned.
 * <br>
 * If the file is not a well formed XML file, an error is returned.
 *
 * @author mmcrockett
 */
public class XmlXPathExists extends SingleCondition<XmlXPathExists> {
    private String xpathExpressionString;
    private XPathExpression xpathExpression;

    private static final String DESCRIPTION = "Check if xml xpath query %s exists in XML file %s";

    public XmlXPathExists() {
    }

    /**
     * @param xpathExpressionString a string that compiles into a {@link javax.xml.xpath.XPathExpression}
     *   if the expression evaluates to an empty string 'false' is returned, otherwise true
     */
    public XmlXPathExists(String xpathExpressionString) {
        setXPathExpression(xpathExpressionString);
    }

    /**
     * The {@link javax.xml.xpath.XPathExpression} whose evaluation indicates
     * the result of this transformation utility.
     * 
     * @param xpathExpressionString a string that compiles into a {@link javax.xml.xpath.XPathExpression}
     *   if the expression evaluates to an empty string 'false' is returned, otherwise true
     *
     * @return this instance
     */
    public XmlXPathExists setXPathExpression(String xpathExpressionString) {
        checkForBlankString("XPath Expression", xpathExpressionString);
        this.xpathExpression = checkXPathCompile(xpathExpressionString);
        this.xpathExpressionString = xpathExpressionString;
        return this;
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            xpathResult = xpathExpression.evaluate(doc, XPathConstants.STRING);

            if (StringUtils.isEmpty((String)xpathResult)) {
                result = TUExecutionResult.value(this, false);
            } else {
                result = TUExecutionResult.value(this, true);
            }
        } catch (ParserConfigurationException|SAXException|IOException e) {
            result = TUExecutionResult.error(this, new TransformationUtilityException("File content could not be parsed properly in XML format", e));
        } catch (TransformationUtilityException e) {
            result = TUExecutionResult.error(this, e);
        } catch (XPathExpressionException e) {
            result = TUExecutionResult.error(this, new TransformationUtilityException("XPathExpression could not be evaluated correctly", e));
        }

        return result;
    }

    private XPathExpression checkXPathCompile(String expression) throws TransformationDefinitionException{
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = null;

        try {
            expr = xpath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new TransformationDefinitionException("XPath expression '" + expression + "' didn't compile correctly.");
        }

        return expr;
    }
}
