package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TOExecutionResult;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

/**
 * Operation to remove a property entry from a properties file
 *
 * @author facarvalho
 */
public class PomRemoveProperty extends AbstractPomOperation<PomRemoveProperty> {

    private static final String DESCRIPTION = "Remove property %s from POM file %s";

    private String propertyName;

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

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, getRelativePath());
    }

    @Override
    protected TOExecutionResult pomExecution(String relativePomFile, Model model) throws XmlPullParserException, IOException {
        TOExecutionResult result = null;

        if(model.getProperties().remove(propertyName) == null) {
            String details = String.format("Property %s could not be found in POM file %s", propertyName, relativePomFile);
            result = TOExecutionResult.noOp(this, details);
        } else {
            String details = String.format("Property %s has been removed from POM file %s", propertyName, relativePomFile);
            result = TOExecutionResult.success(this, details);
        }

        return result;
    }

    @Override
    public PomRemoveProperty clone() throws CloneNotSupportedException {
        PomRemoveProperty pomRemoveProperty = (PomRemoveProperty) super.clone();
        return pomRemoveProperty;
    }

}

