package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.event.Level;

import java.io.File;

/**
 * Utility to provide logging statements during transformation time.
 * Since all it does is logging, it returns as result of execution
 * the log message
 *
 * @author facarvalho
 */
public class Log extends TransformationUtility<Log, String> {

    // TODO Add to Tasks:
    // - create a log method in TransformationTemplate that adds a Log TU
    // - add a flag to TU that says if its result should be saved as transformation
    //   context attribute or not. For example, a TU like Log would turn that OFF
    //   as default, since very likely nobody cares about saving log statements.
    //   By default, it should be ON TU, and it must always be ON for TO, since that
    //   is the way to save their results

    private static Logger logger = LoggerFactory.getLogger(TransformationUtility.class);

    // Log level
    private Level logLevel = Level.INFO;

    // Log message
    private String logMessage;

    // Names of transformation context attributes to be used
    // as arguments for this log statement
    private String[] attributeNames = null;

    /**
     * Utility to provide logging statements during transformation time.
     * Since all it does is logging, it returns as result of execution
     * the log message
     */
    public Log() {
    }

    /**
     * Sets the log level
     *
     * @param logLevel log level
     * @return this transformation utility object
     */
    public Log setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    /**
     * Sets the log message
     *
     * @param logMessage log message
     * @return this transformation utility object
     */
    public Log setLogMessage(String logMessage) {
        this.logMessage = logMessage;
        return this;
    }

    /**
     * Sets names of transformation context attributes to be used
     * as arguments for this log statement
     *
     * @param attributeNames names of transformation context attributes to be
     *                       used as arguments for this log statement
     * @return this transformation utility object
     */
    public Log setAttributeNames(String... attributeNames) {
        this.attributeNames = attributeNames;
        return this;
    }

    @Override
    public String getDescription() {
        return "Logging statement";
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        Object[] attributes = getAttributes(transformedAppFolder, transformationContext);

        switch (logLevel) {
            case ERROR:
                logger.error(logMessage, attributes);
                break;
            case WARN:
                logger.warn(logMessage, attributes);
                break;
            case TRACE:
                logger.trace(logMessage, attributes);
                break;
            case DEBUG:
                logger.debug(logMessage, attributes);
                break;
            case INFO:
            default:
                logger.info(logMessage, attributes);
                break;
        }

        return getName();
    }

    private Object[] getAttributes(File transformedAppFolder, TransformationContext transformationContext) {
        if(attributeNames == null) {
            return new Object[]{};
        }
        Object[] attributes = new Object[attributeNames.length];
        int i = 0;
        Object attribute;
        for(String attributeName : attributeNames) {
            attribute = transformationContext.get(attributeName);
            if(attribute instanceof File) {
                attribute = getRelativePath(transformedAppFolder, (File) attribute);
            }
            attributes[i] = attribute;
            i++;
        }
        return attributes;
    }

}
