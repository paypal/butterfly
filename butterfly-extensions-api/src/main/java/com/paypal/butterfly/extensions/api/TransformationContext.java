package com.paypal.butterfly.extensions.api;

/**
 * Holds meta-data information
 * to be shared among transformation utility objects,
 * allowing communication among them, and helping the
 * transformation process.
 *
 * @author facarvalho
 */
public interface TransformationContext {

    /**
     * Returns the attribute object associated with the key
     * (which is the attribute name), or null, if there is none
     *
     * @param name the transformation context attribute name
     * @return the attribute object
     */
    Object get(String name);

    /**
     * Returns the performing result of the {@link TransformationUtility}
     * identified by the specified name. Notice that it could be a
     * {@link TransformationOperation} too, or any other
     * {@link TransformationUtility} subclass
     *
     * @param utilityName the name of the utility
     * @return the utility performing result
     */
    PerformResult getResult(String utilityName);

}
