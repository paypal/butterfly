package com.paypal.butterfly.extensions.api;

/**
 * Transformation context holds meta-data information
 * to be shared among transformation utility objects,
 * allowing communication among them, and helping the
 * transformation process
 *
 * @author facarvalho
 */
public interface TransformationContext {

    /**
     * Returns the attribute object associated with the key,
     * or null, if there is none
     *
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * Associates a new transformation context attribute with a key.
     * If another attribute had already been associated with same key,
     * it is replaced by the new one.
     *
     * @param key
     * @param attribute
     */
    void put(String key, Object attribute);

}
