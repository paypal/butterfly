package com.paypal.butterfly.extensions.api;

import java.util.List;

/**
 * A transformation template is a set of operations to be
 * applied against an application to be transformed
 *
 * @author facarvalho
 */
public interface TransformationTemplate {

    /**
     * Returns the id of this transformation template
     *
     * @return the id of this transformation template
     */
    String getId();

    /**
     * Returns the transformation template description
     *
     * @return the transformation template description
     */
    String getDescription();

    /**
     * Returns an ordered list of operations to be executed,
     * which defines the actual transformation offered by
     * this template
     *
     * @return the instructions to transform the application,
     *  which is represented by a list of Operation
     */
    List<Operation> getInstructions();

}
