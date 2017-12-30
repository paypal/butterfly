package com.paypal.butterfly.utilities.operations.pom.stax;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

/**
 * Abstract StAX operation, used to stream based XML manipulation.
 *
 * @author facarvalho
 */
public abstract class AbstractStaxOperation<T extends AbstractStaxOperation> extends TransformationOperation<T> {

    protected static final XMLEventFactory eventFactory;
    private static final XMLInputFactory xmlInputFactory;
    private static final XMLOutputFactory xmlOutputFactory;

    private XMLEventReader reader = null;
    private XMLEventWriter writer = null;
    private XMLEvent indentation = null;

    static {
        eventFactory = XMLEventFactory.newInstance();
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlOutputFactory = XMLOutputFactory.newInstance();
    }

    protected final static XMLEvent LINE_FEED = eventFactory.createCharacters("\n");

    /*
     * Consumes events across the provided reader until a the specified event condition is true.
     * As those events are consumed, they are also written to the writer.
     * That last XML event consumed from the reader is written to the writer or not, depending on writeLast parameter.
     * Returns the last XML event consumed, or null, if the end of the reader was reached instead.
     */
    protected XMLEvent copyUntil(XMLEventReader reader, XMLEventWriter writer, EventCondition condition, boolean writeLast) throws XMLStreamException {
        XMLEvent xmlEvent = consumeUntil(reader, writer, condition);

        if (xmlEvent != null && writeLast) {
            writer.add(xmlEvent);
        }

        return xmlEvent;
    }

    /*
     * Consumes events across the provided reader until a the specified event condition is true.
     * Returns the last XML event consumed, or null, if the end of the reader was reached instead.
     */
    protected XMLEvent skipUntil(XMLEventReader reader, EventCondition condition) throws XMLStreamException {
        XMLEvent xmlEvent = consumeUntil(reader, null, condition);

        return xmlEvent;
    }

    /*
     * Consumes events across the provided reader until a the specified event condition is true.
     * As those events are consumed, if a writer is provided, they are also written to the writer.
     * Returns the last XML event consumed, or null, if the end of the reader was reached instead.
     */
    private XMLEvent consumeUntil(XMLEventReader reader, XMLEventWriter writer, EventCondition condition) throws XMLStreamException {
        XMLEvent xmlEvent = null;

        while (reader.hasNext() && xmlEvent == null) {
            XMLEvent event = (XMLEvent)reader.next();
            if (event.isEndDocument()) {
                break;
            }
            if (condition.evaluateEvent(event)) {
                xmlEvent = event;
            } else {
                if (writer != null) {
                    writer.add(event);
                }
            }
        }

        return xmlEvent;
    }

    /*
     * Returns an XML event reader for the file to be modified by this transformation operation.
     * This reader is a singleton, and it is initialized at the first time this method is called.
     * It is important to note though that the file being read is not the actual file to be modified,
     * but a temporary copy of it before any modification is done by this operation.
     */
    protected XMLEventReader getReader(File transformedAppFolder, TransformationContext transformationContext) throws IOException, XMLStreamException {
        if (reader == null) {
            reader = xmlInputFactory.createXMLEventReader(new FileInputStream(getOrCreateReadFile(transformedAppFolder, transformationContext)));
        }
        return reader;
    }

    /*
     * Returns an XML event writer for the file to be modified by this transformation operation.
     * This writer is a singleton, and it is initialized at the first time this method is called.
     */
    protected XMLEventWriter getWriter(File transformedAppFolder, TransformationContext transformationContext) throws FileNotFoundException, XMLStreamException {
        if (writer == null) {
            File fileToBeModified = getAbsoluteFile(transformedAppFolder, transformationContext);
            writer = xmlOutputFactory.createXMLEventWriter(new FileOutputStream(fileToBeModified));
        }
        return writer;
    }

    /*
     * Returns a character XML event corresponding to what this file currently uses as indentation.
     */
    protected XMLEvent getIndentation(File transformedAppFolder, TransformationContext transformationContext) throws IOException, XMLStreamException {
        if (indentation == null) {
            String indentationString = XmlIndentation.getFirst(getOrCreateReadFile(transformedAppFolder, transformationContext));
            indentation = eventFactory.createCharacters(indentationString);
        }
        return indentation;
    }

    /*
     * Write to the given writer the given XML event {@code n} times
     */
    protected static void writeMultiple(XMLEventWriter writer, XMLEvent xmlEvent, int n) throws XMLStreamException {
        for (int i = 1; i <= n; i++) {
            writer.add(xmlEvent);
        }
    }

    @Override
    protected TOExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {

        TOExecutionResult result = null;

        try {
            result = xmlExecution(transformedAppFolder, transformationContext);
        } catch (XmlPullParserException | XMLStreamException | IOException e) {
            File fileToBeModified = getAbsoluteFile(transformedAppFolder, transformationContext);
            String relativePomFile = getRelativePath(transformedAppFolder, fileToBeModified);

            String message = String.format("An error happened when reading XML file %s", relativePomFile);
            TransformationOperationException toe = new TransformationOperationException(message, e);
            result = TOExecutionResult.error(this, toe);
        } catch (Exception e) {
            result = TOExecutionResult.error(this, e);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (XMLStreamException e) {
                result.addWarning(e);
            }
            try {
                if (reader != null) reader.close();
            } catch (XMLStreamException e) {
                result.addWarning(e);
            }
        }

        return result;
    }

    /*
     * This abstract method is supposed to be developed by the subclasses executing XML file operations
     */
    protected abstract TOExecutionResult xmlExecution(File transformedAppFolder, TransformationContext transformationContext) throws XmlPullParserException, XMLStreamException, IOException;

    @Override
    public T clone() {
        AbstractStaxOperation clone = super.clone();
        clone.reader = null;
        clone.writer = null;
        clone.indentation = null;

        return (T) clone;
    }
}
