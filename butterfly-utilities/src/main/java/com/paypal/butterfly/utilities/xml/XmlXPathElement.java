package com.paypal.butterfly.utilities.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.paypal.butterfly.extensions.api.SingleCondition;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

/**
 * Retrieves the data from XML file by using XPath expressions.
 * If no element, nor attribute, is found,
 * {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL} is returned.
 * If the file is not a well formed XML file, an error is returned.
 *
 * @author facarvalho
 */
public class XmlXPathElement extends SingleCondition<XmlXPathElement> {

    private String xpathExpressionString;
    private XPathExpression xpathExpression;

    private static final String DESCRIPTION = "Retrieves data from XML file %s based on XPath expression %s";

    /**
     * Retrieves the data from XML file by using XPath expressions.
     * If no element, nor attribute, is found,
     * {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL} is returned.
     * If the file is not a well formed XML file, an error is returned.
     */
    public XmlXPathElement() {
    }

    /**
     * Retrieves the data from XML file by using XPath expressions.
     * If no element, nor attribute, is found,
     * {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL} is returned.
     * If the file is not a well formed XML file, an error is returned.
     *
     * @param xpathExpressionString a string that compiles into a {@link XPathExpression}
     */
    public XmlXPathElement(String xpathExpressionString) {
        setXPathExpression(xpathExpressionString);
    }

    /**
     * The {@link XPathExpression} to be used to retrieve the data from XML file.
     *
     * @param xpathExpressionString a String that compiles into a {@link XPathExpression}
     *
     * @return this utility instance
     */
    public XmlXPathElement setXPathExpression(String xpathExpressionString) {
        checkForBlankString("XPath Expression", xpathExpressionString);
        this.xpathExpression = checkXPathCompile(xpathExpressionString);
        this.xpathExpressionString = xpathExpressionString;
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

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, getRelativePath(), xpathExpressionString);
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

            if (StringUtils.isEmpty((String) xpathResult)) {
                result = TUExecutionResult.nullResult(this);
            } else {
                result = TUExecutionResult.value(this, xpathResult);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            result = TUExecutionResult.error(this, new TransformationUtilityException("File content could not be parsed properly in XML format", e));
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
            throw new TransformationDefinitionException("XPath expression '" + expression + "' didn't compile correctly.", e);
        }

        return expr;
    }
}
