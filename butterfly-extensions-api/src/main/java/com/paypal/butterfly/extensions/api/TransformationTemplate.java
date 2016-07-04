package com.paypal.butterfly.extensions.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A transformation template is a set of operations to be
 * applied against an application to be transformed
 *
 * @author facarvalho
 */
public abstract class TransformationTemplate<T> {

    private List<TransformationOperation> operationList = new ArrayList<TransformationOperation>();

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
     * Adds a new transformation operation to the end of the list
     *
     * @param operation
     *
     * @return this transformation template
     */
    protected final T add(TransformationOperation operation) {
        operationList.add(operation);
        return (T) this;
    }

    /**
     * Returns a read-only ordered list of transformation operations to be executed,
     * which defines the actual transformation offered by this template
     *
     * @return the list of operations to transform the application,
     */
    public final List<TransformationOperation> getTransformationOperationsList() {
        return Collections.unmodifiableList(operationList);
    }

    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return getName();
    }

}
