package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.File;

/**
 * Provides logging statements during transformation time.
 * Since all it does is logging, it always returns {@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL} as result of
 * execution.
 * <br>
 * If no log level is defined, then it will be set to INFO, except
 * when there is only one attribute and it is null. In this case
 * it will be set to WARNING.
 *
 * @author facarvalho
 */
public class Log extends TransformationUtility<Log> {

    private static Logger logger = LoggerFactory.getLogger(TransformationUtility.class);

    // Log level
    private Level logLevel = null;

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
        setSaveResult(false);
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
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        Object[] attributes = getAttributes(transformedAppFolder, transformationContext);

        if (logLevel == null && attributes.length == 1 && attributes[0] == null) {
            logger.warn(logMessage, attributes);
        } else {
            if (logLevel == null) {
                logLevel = Level.INFO;
            }
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
        }

        return TUExecutionResult.nullResult(this);
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
