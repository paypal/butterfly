package com.paypal.butterfly.extensions.api;

import org.slf4j.event.Level;

import java.util.List;

/**
 * List of {@link TransformationUtility} objects.
 * They are saved in order.
 * 
 * @author facarvalho 
 */
public interface TransformationUtilityList extends TransformationUtilityParent {

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
     */
    String addMultiple(TransformationOperation templateOperation, String... attributes);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list
     *
     * @param logMessage
     */
    void log(String logMessage);


    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list
     *
     * @param logLevel
     * @param logMessage
     */
    void log(Level logLevel, String logMessage);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list
     *
     * @param logMessage
     * @param attributeNames
     */
    void log(String logMessage, String... attributeNames);

    /**
     * Adds a new {@link com.paypal.butterfly.extensions.api.utilities.Log} TU to the list
     *
     * @param logLevel
     * @param logMessage
     * @param attributeNames
     */
    void log(Level logLevel, String logMessage, String... attributeNames);

    /**
     * Returns an immutable ordered list of transformation utilities in this list
     *
     * @return an immutable ordered list of transformation utilities in this list
     */
    List<TransformationUtility> getUtilities();

}
