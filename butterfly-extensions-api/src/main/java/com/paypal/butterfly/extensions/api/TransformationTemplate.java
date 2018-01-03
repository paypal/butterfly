package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.utilities.Log;
import org.slf4j.event.Level;

import java.util.*;

/**
 * A template made of a set of transformation utilities to be applied against an application to be transformed.
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
     * <br>
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
     * <br>
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
     * <br>
     *
     * @param templateOperation a template of transformation operation to be performed
     *                          against all specified files
     * @param attributes one or more transformation context attributes that hold list
     *                   of Files which the transformation operations should perform
     *                   against
     *
     * @return the utility name
     */
    @Override
    public final String addMultiple(TransformationOperation templateOperation, String... attributes) {
        return add(new MultipleOperations(templateOperation).setFiles(attributes));
    }

    /**
     * Execute an utility in a loop a number times specified in {@code iterations}
     *
     * @param utility the utility to be executed each iteration of the loop. To execute more than one, use a {@link TransformationUtilityGroup}
     * @param iterations the number of iterations to be executed
     *
     * @return the utility name
     */
    // TODO
    // This Different than every other method here that adds TUs, this one is not part of TransformationUtlityList.
    // It could be complex to add it to it (because of loops inside of UtilitiesGroups), but to be consistent, it should be added
    public final String loop(TransformationUtility utility, int iterations) {
        return add(new TransformationUtilityLoop(utility).setCondition(iterations));
    }

    /**
     * Execute an utility in a loop while the value in {@link TransformationContext} attribute is true.
     * The defined is specified based on its name, specified in {@code attribute}.
     * If the attribute value is not a boolean, or if non-existent, it will be treated as false.
     *
     * @param utility the utility to be executed each iteration of the loop. To execute more than one, use a {@link TransformationUtilityGroup}
     * @param attribute the name of the transformation context attribute to hold the loop condition
     *
     * @return the utility name
     */
    // TODO
    // This Different than every other method here that adds TUs, this one is not part of TransformationUtlityList.
    // It could be complex to add it to it (because of loops inside of UtilitiesGroups), but to be consistent, it should be added
    public final String loop(TransformationUtility utility, String attribute) {
        return add(new TransformationUtilityLoop(utility).setCondition(attribute));
    }

    /**
     * Execute an utility in a loop while the execution value resulted by {@code condition} is true.
     * The {@link TransformationUtility} object referenced by {@code condition} won't be saved to the {@link TransformationContext},
     * it will be executed exclusively to the scope of this loop execution.
     * Any result other than a boolean true value, including failures, will be treated as false.
     *
     * @param utility the utility to be executed each iteration of the loop. To execute more than one, use a {@link TransformationUtilityGroup}
     * @param condition the {@link UtilityCondition} object whose execution result will be used as the loop condition
     *
     * @return the utility name
     */
    // TODO
    // This Different than every other method here that adds TUs, this one is not part of TransformationUtlityList.
    // It could be complex to add it to it (because of loops inside of UtilitiesGroups), but to be consistent, it should be added
    public final String loop(TransformationUtility utility, UtilityCondition condition) {
        return add(new TransformationUtilityLoop(utility).setCondition(condition));
    }

    @Deprecated
    @Override
    public final void log(String logMessage) {
        info(logMessage);
    }

    @Override
    public final void info(String infoMessage) {
        add(new Log().setLogMessage(infoMessage));
    }

    @Override
    public final void debug(String debugMessage) {
        add(new Log().setLogMessage(debugMessage).setLogLevel(Level.DEBUG));
    }

    @Override
    public final void log(Level logLevel, String logMessage) {
        add(new Log().setLogLevel(logLevel).setLogMessage(logMessage));
    }

    @Deprecated
    @Override
    public final void log(String logMessage, String... attributeNames) {
        info(logMessage, attributeNames);
    }

    @Override
    public final void info(String infoMessage, String... attributeNames) {
        add(new Log().setLogMessage(infoMessage).setAttributeNames(attributeNames));
    }

    @Override
    public final void debug(String debugMessage, String... attributeNames) {
        add(new Log().setLogMessage(debugMessage).setAttributeNames(attributeNames).setLogLevel(Level.DEBUG));
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

    /**
     * Returns the type of the transformed application,
     * Returns null if the application type is unknown.
     * This method is used mostly for meta-data purposes,
     * such as when providing transformation metrics.
     *
     * @return the type of the transformed application
     */
    public String getApplicationType() {
        return null;
    }

    /**
     * Returns the name of the transformed application,
     * Returns null if the application name is unknown.
     * This method is used mostly for meta-data purposes,
     * such as when providing transformation metrics.
     *
     * @return the name of the transformed application
     */
    public String getApplicationName() {
        return null;
    }

}
