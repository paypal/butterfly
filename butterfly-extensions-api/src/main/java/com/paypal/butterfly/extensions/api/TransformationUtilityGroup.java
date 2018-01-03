package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.utilities.Log;
import org.slf4j.event.Level;

import java.io.File;
import java.util.*;

/**
 * Group of transformation utilities. The benefit of grouping them is the ability to, for example, condition the execution of all of them
 * together (by setting {@link #executeIf(String)} to the group), or to loop them, etc.
 *
 * @author facarvalho
 */
public class TransformationUtilityGroup extends TransformationUtility<TransformationUtilityGroup> implements TransformationUtilityList {

    private List<TransformationUtility> utilityList = new ArrayList<TransformationUtility>();

    private Set<String> utilityNames = new HashSet<>();

    private static final String DESCRIPTION = "Transformation utility group";

    // Even though it is redundant to have this default constructor here, since it is
    // the only one (the compiler would have added it implicitly), this is being explicitly
    // set here to emphasize that the public default constructor should always be
    // available by any transformation utility even when additional constructors are present.
    // The reason for that is the fact that one or more of its properties might be set
    // during transformation time, using the TransformationUtility set method
    @SuppressWarnings("PMD.UnnecessaryConstructor")
    public TransformationUtilityGroup() {
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String add(TransformationUtility utility) {
        if (getParent() == null) {
            String exceptionMessage = String.format("Invalid attempt to add transformation utility to utilities group. This group has to be added to a transformation utilities parent first. Add it to another group, or to a transformation template.");
            throw new TransformationDefinitionException(exceptionMessage);
        }
        if (utility.getParent() != null) {
            String exceptionMessage = String.format("Invalid attempt to add already registered transformation utility %s to transformation utility group %s", utility.getName(), getName());
            throw new TransformationDefinitionException(exceptionMessage);
        }
        // TODO
        // Here I am checking for name collisions only in this group, but that is isolated from TUs added to the parent, and vice-versa
        if (utility.getName() != null && utilityNames.contains(utility.getName())) {
            String exceptionMessage = String.format("Invalid attempt to add transformation utility %s to utilities group %s. Its name is already registered", utility.getName(), getName());
            throw new TransformationDefinitionException(exceptionMessage);
        }
        if (!utility.isFileSet()) {
            String exceptionMessage = String.format("Neither absolute, nor relative path, have been set for transformation utility %s", utility.getName());
            throw new TransformationDefinitionException(exceptionMessage);
        }

        int order;
        synchronized(this) {
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

    @Override
    public String add(TransformationUtility utility, String utilityName) {
        utility.setName(utilityName);
        return add(utility);
    }

    @Override
    public String addMultiple(TransformationOperation templateOperation, String... attributes) {
        return add(new MultipleOperations(templateOperation).setFiles(attributes));
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

    @Override
    public List<TransformationUtility> getUtilities() {
        return getChildren();
    }

    @Override
    public List<TransformationUtility> getChildren() {
        return Collections.unmodifiableList(utilityList);
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = TUExecutionResult.value(this, getChildren());
        return result;
    }

    @Override
    public TransformationUtilityGroup clone() {
        TransformationUtilityGroup groupClone = super.clone();
        groupClone.utilityList = new ArrayList<>();
        groupClone.utilityNames = new HashSet<>();
        for (TransformationUtility utility : utilityList) {
            TransformationUtility utilityClone = utility.clone();
            utilityClone.setParent(groupClone, utility.getOrder());
            groupClone.utilityList.add(utilityClone);
            groupClone.utilityNames.add(utilityClone.getName());
        }

        return groupClone;
    }

}
