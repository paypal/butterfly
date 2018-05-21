package com.paypal.butterfly.utilities.operations.xml;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

/**
 * Modify an XML file based on a given XPath expression.
 * <br>
 * It has two different modes of operation:
 * <ol>
 *     <li>If an {@link org.w3c.dom.Element} is provided, the whole node is replaced by given the element.</li>
 *     <li>If a String is provided, then the text of the node is replaced by the given String.</li>
 * </ol>
 * <br>
 * If the XPath expression doesn't compile, or if the file is not a well formed XML file, an error is returned.
 *
 * @author mmcrockett
 */
public class XmlXPathReplace extends TransformationOperation<XmlXPathReplace> {
    private static final String DESCRIPTION = "Replace %s of xpath %s in XML file %s with %s";

    private String xpathExpressionString;
    private XPathExpression xpathExpression;
    private Object replacementObject;
    private boolean textReplace;

    /**
     * @param xpathExpressionString
     *            a string that compiles into a
     *            {@link javax.xml.xpath.XPathExpression} if the expression
     *            evaluates to an empty string 'false' is returned, otherwise
     *            true
     * @param replacementElement
     *            an {@link org.w3c.dom.Element} this is used to replace the
     *            {@link org.w3c.dom.Node} in the search
     */
    public XmlXPathReplace(String xpathExpressionString, Element replacementElement) {
        setXPathExpression(xpathExpressionString);
        setReplacementElement(replacementElement);
    }

    /**
     * @param xpathExpressionString
     *            a string that compiles into a
     *            {@link javax.xml.xpath.XPathExpression} if the expression
     *            evaluates to an empty string 'false' is returned, otherwise
     *            true
     * @param replacementString
     *            a string this is used to replace the text content of the
     *            {@link org.w3c.dom.Node} found in the search
     */
    public XmlXPathReplace(String xpathExpressionString, String replacementString) {
        setXPathExpression(xpathExpressionString);
        setReplacementString(replacementString);
    }

    @Override
    public String getDescription() {
        if (this.textReplace) {
            return String.format(DESCRIPTION, "text", xpathExpressionString, getRelativePath(), replacementObject);
        } else {
            return String.format(DESCRIPTION, "node", xpathExpressionString, getRelativePath(),
                    "user supplied XML Element");
        }
    }

    /**
     * The string that is used to replace the text result of xpath expression.
     * 
     * @param replacementString
     *            a string used to replace text() of xpath.
     *
     * @return this instance
     */
    public XmlXPathReplace setReplacementString(String replacementString) {
        checkForNull("Replacement String", replacementString);
        this.textReplace = true;
        this.replacementObject = replacementString;
        return this;
    }

    /**
     * The {@link org.w3c.dom.Element} that is used to replace the
     * {@link org.w3c.dom.Node} result of xpath expression.
     * 
     * @param replacementElement
     *            an {@link org.w3c.dom.Element} this is used to replace the
     *            {@link org.w3c.dom.Node} in the search
     *
     * @return this instance
     */
    public XmlXPathReplace setReplacementElement(Element replacementElement) {
        checkForNull("Replacement Element", replacementElement);
        this.textReplace = false;
        this.replacementObject = replacementElement;
        return this;
    }

    /**
     * The {@link javax.xml.xpath.XPathExpression} whose evaluation finds the
     * results to replace.
     * 
     * @param xpathExpressionString
     *            a string that compiles into a
     *            {@link javax.xml.xpath.XPathExpression}
     *
     * @return this instance
     */
    public XmlXPathReplace setXPathExpression(String xpathExpressionString) {
        checkForBlankString("XPath Expression", xpathExpressionString);
        this.xpathExpression = checkXPathCompile(xpathExpressionString);
        this.xpathExpressionString = xpathExpressionString;
        return this;
    }

    private XPathExpression checkXPathCompile(String expression) throws TransformationDefinitionException {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = null;

        try {
            expr = xpath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new TransformationDefinitionException(
                    "XPath expression '" + expression + "' didn't compile correctly.");
        }

        return expr;
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File fileToBeChanged = getAbsoluteFile(transformedAppFolder, transformationContext);
        TOExecutionResult result = null;
        NodeList nodes = null;

        try {
            File readFile = getOrCreateReadFile(transformedAppFolder, transformationContext);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(readFile);
            nodes = (NodeList) xpathExpression.evaluate(doc, XPathConstants.NODESET);

            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Node node = nodes.item(idx);

                if (this.textReplace) {
                    node.setTextContent((String) replacementObject);
                } else {
                    Node newNode = doc.importNode((Element) replacementObject, true);
                    node.getParentNode().replaceChild(newNode, node);
                }
            }

            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(new DOMSource(doc), new StreamResult(fileToBeChanged));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            result = TOExecutionResult.error(this,
                    new TransformationOperationException("File content could not be parsed properly in XML format", e));
        } catch (TransformationUtilityException | TransformerException e) {
            result = TOExecutionResult.error(this, e);
        } catch (XPathExpressionException e) {
            result = TOExecutionResult.error(this,
                    new TransformationOperationException("XPathExpression could not be evaluated correctly", e));
        }

        if (result == null) {
            if (nodes != null) {
                String details = String.format(
                        "File %s has had %d node(s) where value replacement was applied based on xml xpath expression '%s'",
                        getRelativePath(), nodes.getLength(), xpathExpressionString);
                if (nodes.getLength() > 0) {
                    result = TOExecutionResult.success(this, details);
                } else {
                    result = TOExecutionResult.noOp(this, details);
                }
            } else {
                result = TOExecutionResult.error(this,
                        new TransformationOperationException("XPathExpression could not be evaluated correctly"));
            }
        }
        return result;
    }

}
