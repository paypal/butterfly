package com.paypal.butterfly.utilities.operations.pom;

import java.util.Objects;
import java.util.Properties;

import org.apache.maven.model.Model;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.operations.AddElement;

/**
 * Adds a new property to a POM file.
 * If the POM file already has a property with same name, the operation will return an error.
 * That behavior can be changed though, see {@link AddElement} for further details.
 *
 * @author facarvalho
 */
public class PomAddProperty extends AbstractPomOperation<PomAddProperty> implements AddElement<PomAddProperty> {

    // TODO
    // Add pre-validation to check, in case version was not set, if plugin
    // is managed or not. If not, fail!

    private static final String DESCRIPTION = "Add property %s=%s to POM file %s";

    private String propertyName;
    private String propertyValue;

    private IfPresent ifPresent = IfPresent.Fail;

    /**
     * Adds a new property to a POM file.
     * If the POM file already has a property with same name, the operation will return an error.
     * That behavior can be changed though, see {@link AddElement} for further details.
     */
    public PomAddProperty() {
    }

    /**
     * Adds a new property to a POM file.
     * If the POM file already has a property with same name, the operation will return an error.
     * That behavior can be changed though, see {@link AddElement} for further details.
     *
     * @param propertyName the name of the property to be set
     * @param propertyValue the value to be set to the property
     */
    public PomAddProperty(String propertyName, String propertyValue) {
        setPropertyName(propertyName);
        setPropertyValue(propertyValue);
    }

    @Override
    public PomAddProperty failIfPresent() {
        ifPresent = IfPresent.Fail;
        return this;
    }

    @Override
    public PomAddProperty warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return this;
    }

    @Override
    public PomAddProperty warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return this;
    }

    @Override
    public PomAddProperty noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return this;
    }

    @Override
    public PomAddProperty overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return this;
    }

    /**
     * Sets the name of the property to be set by this operation
     *
     * @param propertyName the name of the property to be set
     * @return this transformation operation
     */
    public PomAddProperty setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    /**
     * Sets the value of the property to be set by this operation
     *
     * @param propertyValue the value to be set to the property
     * @return this transformation operation
     */
    public PomAddProperty setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    /**
     * Return the name of the property to be set by this operation
     *
     * @return the name of the property to be set
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Returns the value of the property to be set by this operation
     *
     * @return the value to be set to the property
     */
    public String getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, propertyValue, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) {
        Exception warning = null;

        if (model.getProperties() == null ) {
            model.setProperties(new Properties());
        }

        Properties properties = model.getProperties();

        if (properties.getProperty(propertyName) != null) {
            String notAddMessage = "Property " + propertyName + " was not added to POM file " + getRelativePath() + " because it is already present";
            switch (ifPresent) {
                case WarnNotAdd:
                    return TOExecutionResult.warning(this, new TransformationOperationException(notAddMessage));
                case WarnButAdd:
                    warning = new TransformationOperationException("Property " + propertyName + " was already present in POM file " + getRelativePath() + ", but it was overwritten to " + propertyValue);
                    break;
                case NoOp:
                    return TOExecutionResult.noOp(this, notAddMessage);
                case Overwrite:
                    // Nothing to be done here
                    break;
                case Fail:
                    // Fail is the default
                default:
                    return TOExecutionResult.error(this, new TransformationOperationException(notAddMessage));
            }
        }

        properties.put(propertyName, propertyValue);
        String details = String.format("Property %s=%s has been added to POM file %s", propertyName, propertyValue, relativePomFile);
        TOExecutionResult result = TOExecutionResult.success(this, details);

        if (warning != null) {
            result.addWarning(warning);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PomAddProperty)) return false;

        PomAddProperty tu = (PomAddProperty) obj;
        if (!Objects.equals(tu.ifPresent, this.ifPresent)) return false;
        if (!Objects.equals(tu.propertyName, this.propertyName)) return false;
        if (!Objects.equals(tu.propertyValue, this.propertyValue)) return false;

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode(super.hashCode(), ifPresent, propertyName, propertyValue);
    }

}