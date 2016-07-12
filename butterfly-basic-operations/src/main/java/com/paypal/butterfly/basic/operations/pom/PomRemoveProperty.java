package com.paypal.butterfly.basic.operations.pom;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;

import java.io.File;

/**
 * Operation to remove a property entry from a properties file
 *
 * @author facarvalho
 */
public class PomRemoveProperty extends TransformationOperation<PomRemoveProperty> {

    private static final String DESCRIPTION = "Remove property %s from POM file %s";

    private String propertyName;

    /**
     * Operation to remove a property entry from a properties file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     */
    private PomRemoveProperty(String relativePath) {
        super(relativePath);
    }

    /**
     * Operation to remove a property entry from a properties file
     *
     * @param relativePath
     * @see {@link #setRelativePath(String)}
     * @param propertyName property to be removed
     */
    public PomRemoveProperty(String relativePath, String propertyName) {
        this(relativePath);
        this.propertyName = propertyName;
   }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, propertyName, getRelativePath());
    }

    @Override
    protected String execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        // TODO

        return null;
    }

}
