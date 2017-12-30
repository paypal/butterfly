package com.paypal.butterfly.utilities.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.ChangeOrRemoveElement;
import org.apache.maven.model.Model;

/**
 * Removes a property entry from a POM file.
 *
 * @author facarvalho
 */
public class PomRemoveProperty extends AbstractPomOperation<PomRemoveProperty> implements ChangeOrRemoveElement<PomRemoveProperty> {

    private static final String DESCRIPTION = "Remove property %s from POM file %s";

    private String propertyName;

    private IfNotPresent ifNotPresent = IfNotPresent.Fail;

    public PomRemoveProperty() {
    }

    /**
     * Operation to remove a property entry from a properties file
     *
     * @param propertyName property to be removed
     */
    public PomRemoveProperty(String propertyName) {
        setPropertyName(propertyName);
    }

    public PomRemoveProperty setPropertyName(String propertyName) {
        checkForBlankString("Property Name", propertyName);
        this.propertyName = propertyName;
        return this;
    }

    @Override
    public PomRemoveProperty failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return this;
    }

    @Override
    public PomRemoveProperty warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return this;
    }

    @Override
    public PomRemoveProperty noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
        return this;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        TOExecutionResult result = null;

        if(model.getProperties().remove(propertyName) == null) {
            String details = String.format("Property %s has not been removed from POM file %s because it is not present", propertyName, relativePomFile);
            switch (ifNotPresent) {
                case Warn:
                    result = TOExecutionResult.warning(this, new TransformationOperationException(details));
                    break;
                case NoOp:
                    result = TOExecutionResult.noOp(this, details);
                    break;
                case Fail:
                    // Fail is the default
                default:
                    result = TOExecutionResult.error(this, new TransformationOperationException(details));
                    break;
            }
        } else {
            String details = String.format("Property %s has been removed from POM file %s", propertyName, relativePomFile);
            result = TOExecutionResult.success(this, details);
        }

        return result;
    }

}

