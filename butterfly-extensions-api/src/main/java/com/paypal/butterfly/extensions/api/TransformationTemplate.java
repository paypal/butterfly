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
     * Returns the name of this transformation template
     *
     * @return the name of this transformation template
     */
    String getName();

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
