package com.paypal.butterfly.extensions.api;

/**
 * A transformation operation
 *
 * @author facarvalho
 */
public interface Operation {

    /**
     * Returns the transformation operation name
     *
     * @return the transformation operation name
     */
    String getName();

    /**
     * Returns a description about the transformation
     * operation
     *
     * @return a description about the transformation
     * operation
     */
    String getDescription();

    /**
     * Performs the transformation operation against
     * the application to be transformed
     */
    void perform();

}
