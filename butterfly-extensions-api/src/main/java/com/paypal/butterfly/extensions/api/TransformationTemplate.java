package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.utilities.Log;
import org.slf4j.event.Level;

import java.util.*;

/**
 * A transformation template is a set of transformation
 * utilities to be applied against an application to be transformed
 *
 * @author facarvalho
 */
public abstract class TransformationTemplate implements TransformationUtilityList {

    private List<TransformationUtility> utilityList = new ArrayList<>();

    private Set<String> utilityNames = new HashSet<>();

    private String name = getExtensionClass().getSimpleName() + ":" + getClass().getSimpleName();;

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
    @Override
    public final String add(TransformationUtility utility) {
        if (utility.getParent() != null) {
            String exceptionMessage = String.format("Invalid attempt to add already registered transformation utility %s to template transformation utility %s", utility.getName(), name);
            throw new  TransformationDefinitionException(exceptionMessage);
        }
        // TODO
        // Here I should check the TUs inside of utilities groups and multiple operations as well
        if (utility.getName() != null && utilityNames.contains(utility.getName())) {
            String exceptionMessage = String.format("Invalid attempt to add transformation utility %s to template transformation utility %s. Its name is already registered", utility.getName(), name);
            throw new  TransformationDefinitionException(exceptionMessage);
        }
        if (!utility.isFileSet()) {
            String exceptionMessage = String.format("Neither absolute, nor relative path, have been set for transformation utility %s", utility.getName());
            throw new  TransformationDefinitionException(exceptionMessage);
        }

        int order;
        synchronized (this) {
            utilityList.add(utility);

            // This is the order of execution of this utility
            // Not to be confused with the index of the element in the list,
            // Since the first utility will be assigned order 1 (not 0)
            order = utilityList.size();
        }

        utility.setParent(this, order);

        utilityNames.add(utility.getName());

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
    @Override
    public final String add(TransformationUtility utility, String utilityName) {
        utility.setName(utilityName);
        return add(utility);
    }

    /**
     * Adds a special transformation utility to perform multiple transformation operations against
     * multiple files specified as a list, held as a transformation context attribute
     * </br>
     *
     * @param templateOperation a template of transformation operation to be performed
     *                          against all specified files
     * @param attributes one or more transformation context attributes that hold list
     *                   of Files which the transformation operations should perform
     *                   against
     */
    @Override
    public final String addMultiple(TransformationOperation templateOperation, String... attributes) {
        return add(new MultipleOperations(templateOperation).setFiles(attributes));
    }

    @Override
    public final void log(String logMessage) {
        add(new Log().setLogMessage(logMessage));
    }

    @Override
    public final void log(Level logLevel, String logMessage) {
        add(new Log().setLogLevel(logLevel).setLogMessage(logMessage));
    }

    @Override
    public final void log(String logMessage, String... attributeNames) {
        add(new Log().setLogMessage(logMessage).setAttributeNames(attributeNames));
    }

    @Override
    public final void log(Level logLevel, String logMessage, String... attributeNames) {
        add(new Log().setLogLevel(logLevel).setLogMessage(logMessage).setAttributeNames(attributeNames));
    }

    /**
     * Returns a read-only ordered list of transformation utilities to be executed,
     * which defines the actual transformation offered by this template
     *
     * @return the list of utilities to transform the application,
     */
    @Override
    public final List<TransformationUtility> getUtilities() {
        return Collections.unmodifiableList(utilityList);
    }

    @Override
    public List<TransformationUtility> getChildren() {
        return getUtilities();
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
