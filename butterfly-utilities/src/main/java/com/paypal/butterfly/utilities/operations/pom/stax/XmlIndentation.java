package com.paypal.butterfly.utilities.operations.pom.stax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Utility class to find out indentation usage in a given XML file
 *
 * @author facarvalho
 */
class XmlIndentation {

    private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    /**
     * Returns a String that represents the indentation used in the very first
     * tag, inside the root tag, in the given XML file.
     * Returns {@code null} if the root tag has no children,
     * or if the first tag is at the same line as the root tag.
     *
     * @param xmlFile XML file to be evaluated
     * @return a String that represents the indentation used in the very first tag,
     * inside the root tag, in the given XML file
     * @throws FileNotFoundException if the given file does not exist
     * @throws XMLStreamException if an error occurs when reading the XML file
     */
    static String getFirst(File xmlFile) throws XMLStreamException, FileNotFoundException {

        // first occurrences of a set of ignored characters in between elements
        String firstElementsGap = null;

        boolean rootElementConsumed = false;
        boolean firstElementConsumed = false;

        XMLEvent xmlEvent;
        XMLEventReader reader = null;

        try {
            reader = xmlInputFactory.createXMLEventReader(new FileInputStream(xmlFile));

            while (reader.hasNext()) {
                xmlEvent = reader.nextEvent();
                if (xmlEvent.isCharacters() && !xmlEvent.asCharacters().isCData()) {
                    firstElementsGap = xmlEvent.asCharacters().getData();
                } else if (xmlEvent.isStartElement()) {
                    if (rootElementConsumed) {
                        firstElementConsumed = true;
                        break;
                    } else {
                        rootElementConsumed = true;
                    }
                }
            }
            reader.close();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        String indentationSample = null;

        if (firstElementConsumed && firstElementsGap != null) {
            int i = firstElementsGap.lastIndexOf('\n') + 1;
            indentationSample = firstElementsGap.substring(i);
        }

        return indentationSample;
    }

}
