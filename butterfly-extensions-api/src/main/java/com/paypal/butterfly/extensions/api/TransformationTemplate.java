package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.utilities.Log;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A transformation template is a set of transformation
 * utilities to be applied against an application to be transformed
 *
 * @author facarvalho
 */
public abstract class TransformationTemplate<TT> implements TransformationUtilityParent {

    private List<TransformationUtility> utilityList = new ArrayList<TransformationUtility>();

    private String name = getExtensionClass().getSimpleName() + ":" + getClass().getSimpleName();;

    private int operationsCount = 0;

    /**
     * Returns the class of the extension this transformation
     * template belongs to
     *
     * @return the class of the extension this transformation
     *  template belongs to
     */
    public abstract Class<? extends Extension> getExtensionClass();

    /**
     * Returns the transformation template description
     *
     * @return the transformation template description
     */
    public abstract String getDescription();

    /**
     * Adds a new transformation utility to the end of the list.
     * Also, if no name has been set for this utility yet, the template
     * names the utility based on this template's name and the order of
     * execution.
     * </br>
     * This method also register the template within the utility, which
     * means a transformation utility instance can be registered to
     * ONLY ONE transformation template
     *
     * @param utility the utility to be added
     *
     * @return the utility name
     */
    protected final String add(TransformationUtility utility) {
        if (utility.getParent() != null) {
            String exceptionMessage = String.format("Invalid attempt to add already registered transformation utility %s to template transformation utility %s", utility.getName(), name);
            TransformationDefinitionException exception = new  TransformationDefinitionException(exceptionMessage);
            throw exception;
        }

        int order;
        synchronized (this) {
            utilityList.add(utility);

            // This is the order of execution of this utility
            // Not to be confused with the index of the element in the list,
            // Since the first utility will be assigned order 1 (not 0)
            order = utilityList.size();

            if(utility instanceof TransformationOperation) {
                operationsCount++;
            }
        }

        utility.setParent(this, order);

        if (utility.getRelativePath() == null && utility.getAbsoluteFileFromContextAttribute() == null) {
            String exceptionMessage = String.format("Neither absolute, nor relative path, have been set for transformation utility %s", utility.getName());
            TransformationDefinitionException exception = new  TransformationDefinitionException(exceptionMessage);
            throw exception;
        }

        return utility.getName();
    }

    /**
     * Adds a new transformation utility to the end of the list.
     * It sets the utility name before adding it though.
     * </br>
     * This method also register the template within the utility, which
     * means a transformation utility instance can be registered to
     * ONLY ONE transformation template
     *
     * @param utility the utility to be added
     * @param utilityName the name to be set to the utility before adding it
     *
     * @return the utility name
     */
    protected final String add(TransformationUtility utility, String utilityName) {
        utility.setName(utilityName);
        return add(utility);
    }

    protected final void log(String logMessage) {
        add(new Log().setLogMessage(logMessage));
    }

    protected final void log(Level logLevel, String logMessage) {
        add(new Log().setLogLevel(logLevel).setLogMessage(logMessage));
    }

    protected final void log(String logMessage, String... attributeNames) {
        add(new Log().setLogMessage(logMessage).setAttributeNames(attributeNames));
    }

    protected final void log(Level logLevel, String logMessage, String... attributeNames) {
        add(new Log().setLogLevel(logLevel).setLogMessage(logMessage).setAttributeNames(attributeNames));
    }

    /**
     * Returns the number of transformation operations to be executed by this
     * transformation template
     *
     * @return
     */
    public int getOperationsCount() {
        return operationsCount;
    }

    /**
     * Returns a read-only ordered list of transformation utilities to be executed,
     * which defines the actual transformation offered by this template
     *
     * @return the list of utilities to transform the application,
     */
    public final List<TransformationUtility> getTransformationUtilitiesList() {
        return Collections.unmodifiableList(utilityList);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return getName();
    }

}
