package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Transformation context implementation
 *
 * @see {@link TransformationContext}
 *
 * @author facarvalho
 */
public class TransformationContextImpl implements TransformationContext {

    private Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public void put(String key, Object attribute) {
        attributes.put(key, attribute);
    }

}
