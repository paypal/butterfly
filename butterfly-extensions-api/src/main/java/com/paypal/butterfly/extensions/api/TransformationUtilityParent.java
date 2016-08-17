package com.paypal.butterfly.extensions.api;

/**
 * Marker interface for every type that can be assigned as
 * the parent of a {@link com.paypal.butterfly.extensions.api.TransformationUtility}
 *
 * @author facarvalho
 */
public interface TransformationUtilityParent {

    /**
     * Return the name of this transformation utility parent
     *
     * @return the name of this transformation utility parent
     */
    String getName();

}
