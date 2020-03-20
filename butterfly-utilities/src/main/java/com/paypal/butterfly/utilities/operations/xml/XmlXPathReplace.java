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
 * It has the following different modes of operation:
 * <ol>
 *     <li>If an {@link org.w3c.dom.Element} is provided, every matched node is replaced by given the element.</li>
 *     <li>If a String is provided, the text of every matched node is replaced by the given String.</li>
 *     <li>If neither {@link org.w3c.dom.Element} nor String are provided, every matched node is removed.</li>
 * </ol>
 * <br>
 * If the XPath expression doesn't compile, or if the file is not a well formed XML file, an error is returned.
 *
 * @author mmcrockett
 */
public class XmlXPathReplace extends TransformationOperation<XmlXPathReplace> {

    private static final String DESCRIPTION = "Replace %s of XPath %s in XML file %s with %s";

    private String xpathExpressionString;
    private XPathExpression xpathExpression;
    private Object replacementObject;
    private Mode mode;

    private enum Mode {
        TextReplace, ElementReplace, Removal
    }

    /**
     * Modify an XML file based on a given XPath expression.
     * Since neither {@link org.w3c.dom.Element} nor String are provided, the matched node will be removed.
     * <br>
     * If the XPath expression doesn't compile, or if the file is not a well formed XML file, an error is returned.
     *
     * @param xpathExpressionString a String that compiles into a {@link javax.xml.xpath.XPathExpression}
     */
    public XmlXPathReplace(String xpathExpressionString) {
        setXPathExpression(xpathExpressionString);
        mode = Mode.Removal;
    }

    /**
     * Modify an XML file based on a given XPath expression.
     * Since an {@link org.w3c.dom.Element} is provided, the whole node is replaced by given the element.
     * <br>
     * If the XPath expression doesn't compile, or if the file is not a well formed XML file, an error is returned.
     *
     * @param xpathExpressionString a String that compiles into a {@link javax.xml.xpath.XPathExpression}
     * @param replacementElement
     *            an {@link org.w3c.dom.Element} this is used to replace the
     *            {@link org.w3c.dom.Node} in the search
     */
    public XmlXPathReplace(String xpathExpressionString, Element replacementElement) {
        setXPathExpression(xpathExpressionString);
        setReplacementElement(replacementElement);
    }

    /**
     * Modify an XML file based on a given XPath expression.
     * <br>
     * It has the following different modes of operation:
     * Since a String is provided, then the text of the node is replaced by the given String.
     * <br>
     * If the XPath expression doesn't compile, or if the file is not a well formed XML file, an error is returned.
     *
     * @param xpathExpressionString a String that compiles into a {@link javax.xml.xpath.XPathExpression}
     * @param replacementString
     *            a String this is used to replace the text content of the
     *            {@link org.w3c.dom.Node} found in the search
     */
    public XmlXPathReplace(String xpathExpressionString, String replacementString) {
        setXPathExpression(xpathExpressionString);
        setReplacementString(replacementString);
    }

    @Override
    public String getDescription() {
        String description = null;

        switch (mode) {
            case TextReplace:
                description = String.format(DESCRIPTION, "text", xpathExpressionString, getRelativePath(), replacementObject);
                break;
            case ElementReplace:
                description = String.format(DESCRIPTION, "node", xpathExpressionString, getRelativePath(), "user supplied XML Element");
                break;
            case Removal:
                description = String.format("Remove every node that matches XPath %s in XML file %s", xpathExpressionString, getRelativePath());
                break;
        }

        return description;
    }

    /**
     * The String that is used to replace the text result of xpath expression.
     * 
     * @param replacementString
     *            a String used to replace text() of xpath.
     *
     * @return this instance
     */
    public XmlXPathReplace setReplacementString(String replacementString) {
        checkForBlankString("Replacement String", replacementString);
        mode = Mode.TextReplace;
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
        mode = Mode.ElementReplace;
        this.replacementObject = replacementElement;
        return this;
    }

    /**
     * The {@link javax.xml.xpath.XPathExpression} whose evaluation finds the
     * results to replace.
     * 
     * @param xpathExpressionString
     *            a String that compiles into a
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
            throw new TransformationDefinitionException("XPath expression '" + expression + "' didn't compile correctly.");
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

            if (nodes.getLength() > 0) {
                for (int idx = 0; idx < nodes.getLength(); idx++) {
                    Node node = nodes.item(idx);
                    switch (mode) {
                        case TextReplace:
                            node.setTextContent((String) replacementObject);
                            break;
                        case ElementReplace:
                            Node newNode = doc.importNode((Element) replacementObject, true);
                            node.getParentNode().replaceChild(newNode, node);
                            break;
                        case Removal:
                            node.getParentNode().removeChild(node);
                            break;
                    }
                }
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(new DOMSource(doc), new StreamResult(fileToBeChanged));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            result = TOExecutionResult.error(this,
                    new TransformationOperationException("File content could not be parsed properly in XML format", e));
        } catch (TransformerException e) {
            result = TOExecutionResult.error(this, e);
        } catch (XPathExpressionException e) {
            result = TOExecutionResult.error(this,
                    new TransformationOperationException("XPathExpression could not be evaluated correctly", e));
        }

        if (result == null) {
            if (nodes != null) {
                String details = String.format(
                        "File %s has had %d node(s) where modification was applied based on xml xpath expression '%s'",
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
