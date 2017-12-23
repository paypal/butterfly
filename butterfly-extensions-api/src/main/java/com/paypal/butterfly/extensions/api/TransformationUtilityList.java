package com.paypal.butterfly.extensions.api;

import org.slf4j.event.Level;

import java.util.List;

/**
 * List of {@link TransformationUtility} objects.
 * They are saved in order.
 * 
 * @author facarvalho 
 */
interface TransformationUtilityList extends TransformationUtilityParent {

    /**
     * Adds a new transformation utility to the end of the list.
     * Also, if no name has been set for this utility yet, a name
     * will be set to it.
     *
     * @param utility the utility to be added
     *
     * @return the utility name
     */
    String add(TransformationUtility utility);

    /**
     * Adds a new transformation utility to the end of the list.
     * It sets the utility name before adding it though.
     *
     * @param utility the utility to be added
     * @param utilityName the name to be set to the utility before adding it
     *
     * @return the utility name
     */
    String add(TransformationUtility utility, String utilityName);

    /**
     * Adds a special transformation utility to perform multiple transformation operations against
     * multiple files specified as a list, held as a transformation context attribute
     * <br>
     *
     * @param templateOperation a template of transformation operation to be performed
     *                          against all specified files
     * @param attributes one or more transformation context attributes that hold list
     *                   of Files which the transformation operations should perform
     *                   against
     * @return the name of the special transformation utility that performs multiple transformation operations
     */
    String addMultiple(TransformationOperation templateOperation, String... attributes);

    /**
     * This method has been deprecated. Use {@link #info(String)} instead.
     *
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list
     *
     * @param logMessage the message to be logged
     */
    @Deprecated
    void log(String logMessage);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list,
     * setting its log level to INFO
     *
     * @param infoMessage the info message to be logged
     */
    void info(String infoMessage);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list,
     * setting its log level to DEBUG
     *
     * @param debugMessage the debug message to be logged
     */
    void debug(String debugMessage);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list
     *
     * @param logLevel the log level
     * @param logMessage the message to be logged
     */
    void log(Level logLevel, String logMessage);

    /**
     * This method has been deprecated. Use {@link #info(String, String...)} instead.
     *
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list.
     * The log messages may contain placeholders to be replaced by transformation context
     * attribute values. Use {@code {}} as placeholder marker.
     *
     * @param logMessage the message to be logged, containing {@code {}} placeholders to be replaced by
     *                   transformation context attribute values
     * @param attributeNames an array of names of transformation context attributes, whose values
     *                       are going to be used in the log message
     */
    @Deprecated
    void log(String logMessage, String... attributeNames);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list,
     * setting its log level to INFO.
     * The log messages may contain placeholders to be replaced by transformation context
     * attribute values. Use {@code {}} as placeholder marker.
     *
     * @param infoMessage the info message to be logged, containing {@code {}} placeholders to be replaced by
     *                   transformation context attribute values
     * @param attributeNames an array of names of transformation context attributes, whose values
     *                       are going to be used in the log message
     */
    void info(String infoMessage, String... attributeNames);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list,
     * setting its log level to DEBUG.
     * The log messages may contain placeholders to be replaced by transformation context
     * attribute values. Use {@code {}} as placeholder marker.
     *
     * @param debugMessage the debug message to be logged, containing {@code {}} placeholders to be replaced by
     *                   transformation context attribute values
     * @param attributeNames an array of names of transformation context attributes, whose values
     *                       are going to be used in the log message
     */
    void debug(String debugMessage, String... attributeNames);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list.
     * The log messages may contain placeholders to be replaced by transformation context
     * attribute values. Use {@code {}} as placeholder marker.
     *
     * @param logLevel the log level
     * @param logMessage the message to be logged, containing {@code {}} placeholders to be replaced by
     *                   transformation context attribute values
     * @param attributeNames an array of names of transformation context attributes, whose values
     *                       are going to be used in the log message
     */
    void log(Level logLevel, String logMessage, String... attributeNames);

    /**
     * Returns an immutable ordered list of transformation utilities in this list
     *
     * @return an immutable ordered list of transformation utilities in this list
     */
    List<TransformationUtility> getUtilities();

}
