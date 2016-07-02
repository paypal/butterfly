package com.paypal.butterfly.extensions.api;

import java.util.List;

/**
 * A transformation template is a set of operations to be
 * applied against an application to be transformed
 *
 * @author facarvalho
 */
public abstract class TransformationTemplate {

    private String name = getExtensionClass().getSimpleName() + "-" + getClass().getSimpleName();;

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
     * Returns an ordered list of operations to be executed,
     * which defines the actual transformation offered by
     * this template
     *
     * @return the instructions to transform the application,
     *  which is represented by a list of Operation
     */
    public abstract List<Operation> getInstructions();

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
