package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.PerformResult;
import com.paypal.butterfly.extensions.api.Result;
import com.paypal.butterfly.extensions.api.TransformationContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Transformation context implementation
 *
 * @see {@link TransformationContext}
 *
 * @author facarvalho
 */
class TransformationContextImpl implements TransformationContext {

    private Map<String, Object> attributes = new HashMap<>();
    private Map<String, PerformResult> results = new HashMap<>();

    @Override
    public Object get(String name) {
        if(StringUtils.isBlank(name)) {
            // TODO replace this by a better exception type.
            // TransformationContextException could be a good one, however, according to the link below,
            // it is a checked exception, but we definitely need a runtime exception here.
            // https://engineering.paypalcorp.com/confluence/display/RaptorServices/Butterfly#Butterfly-Butterflyexceptions
            throw new IllegalArgumentException("Transformation context attribute key cannot be null");
        }
        return attributes.get(name);
    }

    @Override
    public PerformResult getResult(String utilityName) {
        if(StringUtils.isBlank(utilityName)) {
            throw new IllegalArgumentException("Result key cannot be null");
        }
        return results.get(utilityName);
    }

    /**
     * Puts a new transformation context attribute, using its name as key.
     * If another attribute had already been associated with same key,
     * it is replaced by the new one.
     *
     * @param name the transformation context attribute name
     * @param attributeObject the attribute object
     */
    void put(String name, Object attributeObject) {
        // TODO
//        if(StringUtils.isBlank(name) || attributeObject == null || (attributeObject instanceof String && StringUtils.isBlank((String) attributeObject))) {
//            // TODO replace this by a better exception type.
//            // TransformationContextException could be a good one, however, according to the link below,
//            // it is a checked exception, but we definitely need a runtime exception here.
//            // https://engineering.paypalcorp.com/confluence/display/RaptorServices/Butterfly#Butterfly-Butterflyexceptions
//            throw new IllegalArgumentException("Transformation context attribute name and object cannot be null nor blank");
//        }
        attributes.put(name, attributeObject);
    }

    /**
     * Puts a new transformation utility result, using its name as key.
     * If another result had already been associated with same key,
     * it is replaced by the new one.
     *
     * @param name the transformation utility name
     * @param resultObject the result object
     */
    void putResult(String name, PerformResult resultObject) {
        if(StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Result name cannot be null nor blank");
        }
        if(resultObject == null) {
            throw new IllegalArgumentException("Result object cannot be null");
        }
        results.put(name, resultObject);
    }
}
