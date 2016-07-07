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
public abstract class TransformationTemplate<TT> {

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
     * Adds a new transformation operation to the end of the list.
     * Also, if no name has been set for this operation yet, the template
     * names the operation based on this template's name and the order of
     * execution.
     * </br>
     * This method also register the template within the operation, which
     * means a transformation operation instance can be registered to
     * ONLY ONE transformation template
     *
     * @param operation
     */
    protected final void add(TransformationOperation operation) {
        if (operation.getTemplate() != null) {
            throw new IllegalStateException("Invalid attempt to add an already registered operation to a template");
        }

        int order;
        synchronized (this) {
            operationList.add(operation);

            // This is the order of execution of this operation
            // Not to be confused with the index of the element in the list,
            // Since the first operation will be assigned order 1 (not 0)
            order = operationList.size();

        }

        operation.setTemplate(this, order);
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
