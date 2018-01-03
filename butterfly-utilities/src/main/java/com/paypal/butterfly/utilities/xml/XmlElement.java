package com.paypal.butterfly.utilities.xml;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Retrieves the value of an element,
 * or one of its attributes, in a XML file.
 * <br>
 * <strong>Note: if more than one element match
 * the specified XML element, the very first
 * one will be used</strong>
 * <br>
 * If no element, nor attribute, is found,
 * {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL} is returned.
 * If the file is not a well formed XML file, an error is returned.
 *
 * @author facarvalho
 */
public class XmlElement extends TransformationUtility<XmlElement> {

    private String xmlElement;
    private String attribute;

    private static final String DESCRIPTION = "Retrieve the value of element %s in XML file %s";

    private static final Pattern XML_ELEMENT_SPLIT_REGEX_PATTERN = Pattern.compile("\\.");

    /**
     * Retrieves the value of an element,
     * or one of its attributes, in a XML file.
     * <br>
     * <strong>Note: if more than one element match
     * the specified XML element, the very first
     * one will be used</strong>
     */
    public XmlElement() {
    }

    /**
     * Retrieves the value of an element,
     * or one of its attributes, in a XML file.
     * <br>
     * <strong>Note: if more than one element match
     * the specified XML element, the very first
     * one will be used</strong>
     * <br>
     * Specify in {@code xmlElement} the XML element whose value, or an attribute, should be
     * the result of this transformation utility. The element specified
     * here should be set based on a path containing all its
     * parent elements separated by '.'. See the example bellow.
     * <br>
     * To retrieve the value of the child name, set {@code xmlElement}
     * to {@code person.child.name}. In this example,
     * that would return {@code Gabriela}
     * <br>
     * {@code
     *  <?xml version="1.0" encoding="UTF-8"?>
     *  <person>
     *      <name>Bruna</name>
     *      <child>
     *          <name>Gabriela</name>
     *      </child>
     *  </peson>
     * }
     *
     * @param xmlElement the XML element whose value, or an attribute, should be
     * the result of this transformation utility
     */
    public XmlElement(String xmlElement) {
        setXmlElement(xmlElement);
    }

    /**
     * The XML element whose value, or an attribute, should be
     * the result of this transformation utility. The element specified
     * here should be set based on a path containing all its
     * parent elements separated by '.'. See the example bellow.
     * <br>
     * To retrieve the value of the child name, set {@code xmlElement}
     * to {@code person.child.name}. In this example,
     * that would return {@code Gabriela}
     * <br>
     * {@code
     *  <?xml version="1.0" encoding="UTF-8"?>
     *  <person>
     *      <name>Bruna</name>
     *      <child>
     *          <name>Gabriela</name>
     *      </child>
     *  </peson>
     * }
     *
     * @param xmlElement the XML element whose value, or an attribute, should be
     * the result of this transformation utility
     *
     * @return this instance
     */
    public XmlElement setXmlElement(String xmlElement) {
        checkForBlankString("XML Element", xmlElement);
        this.xmlElement = xmlElement;
        return this;
    }

    /**
     * Set the name of the XML element attribute
     * to be retrieved. If null, the element
     * value will be retrieved instead
     *
     * @param attribute the name of the XML element attribute
     * to be retrieved
     * @return this instance
     */
    public XmlElement setAttribute(String attribute) {
        checkForEmptyString("Attribute", attribute);
        this.attribute = attribute;
        return this;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, xmlElement, getRelativePath());
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        File xmlFile = getAbsoluteFile(transformedAppFolder, transformationContext);
        TUExecutionResult result;

        try {
            Node node;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            node = findNode(doc.getChildNodes(), XML_ELEMENT_SPLIT_REGEX_PATTERN.split(xmlElement), 0);

            if (node == null) {
                result = TUExecutionResult.nullResult(this);
            } else {
                String value;
                if(attribute == null) {
                    value = node.getTextContent();
                    result = TUExecutionResult.value(this, value);
                } else {
                    Node attributeNode = node.getAttributes().getNamedItem(attribute);
                    if (attributeNode == null) {
                        result = TUExecutionResult.nullResult(this);
                    } else {
                        value = node.getAttributes().getNamedItem(attribute).getTextContent();
                        result = TUExecutionResult.value(this, value);
                    }
                }
            }
        } catch (ParserConfigurationException|SAXException|IOException e) {
            result = TUExecutionResult.error(this, new TransformationUtilityException("File content could not be parsed properly in XML format", e));
        } catch (TransformationUtilityException e) {
            result = TUExecutionResult.error(this, e);
        }


        return result;
    }

    private Node findNode(NodeList nodeList, String[] xmlElementPath, int i) throws TransformationUtilityException {
        String nextElement = xmlElementPath[i];
        Node node = null;

        for(int j = 0; j < nodeList.getLength(); j++) {
            if(nodeList.item(j).getNodeName().equals(nextElement)) {
                node = nodeList.item(j);
                break;
            }
        }
        if(node == null) {
            return null;
        }

        int next = i + 1;
        if(xmlElementPath.length == next) {
            return node;
        }

        return findNode(node.getChildNodes(), xmlElementPath, next);
    }

}
